package org.mybatis.jpetstore.controller;

import org.mybatis.jpetstore.DTO.PendingOrder;
import org.mybatis.jpetstore.domain.Account;
import org.mybatis.jpetstore.domain.Cart;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/")
public class OrderController {
    private static final String REDIRECT_BASE_URL="http://localhost:8080";
    // 스레드 세이프한 해시맵 생성
    private static final ConcurrentHashMap<Integer, Pair<CompletableFuture<ModelAndView>, Order>> asyncRequests = new ConcurrentHashMap<>();
    @Autowired
    OrderService orderService;

    @GetMapping("/listOrders")
    public String listOrders(HttpSession session, HttpServletRequest request, RedirectAttributes redirect) {
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            String msg = "You must sign on before attempting to check out.  Please sign on and try checking out again.";
            redirect.addAttribute("msg", msg);
            return "redirect:" + REDIRECT_BASE_URL + "/account/signonForm";
        }
        List<Order> orderList = orderService.getOrdersByUsername(account.getUsername());
        request.setAttribute("orderList", orderList);
        return "order/ListOrders";
    }

    @GetMapping("/newOrderForm")
    public String newOrderForm(HttpServletRequest req, HttpSession session, RedirectAttributes redirect) {
        Account account = (Account) session.getAttribute("account");
        Cart cart = (Cart) session.getAttribute("cart");
        if (account == null) {
            String msg = "You must sign on before attempting to check out.  Please sign on and try checking out again.";
            redirect.addAttribute("msg", msg);
            return "redirect:" + REDIRECT_BASE_URL + "/account/signonForm";
        }
        else if (cart != null) {
            Order order = new Order();
            order.initOrder(account, cart);
            session.setAttribute("order", order);
            return "order/NewOrderForm";
        }
        else {
            String msg = "An order could not be created because a cart could not be found.";
            req.setAttribute("msg", msg);
            return "common/Error";
        }
    }

    @PostMapping("/newOrder")
    public CompletableFuture<ModelAndView> newOrder(Order order, @RequestParam(required = false) boolean shippingAddressRequired, @RequestParam(required = false) boolean confirmed, @RequestParam(required = false) boolean changeShipInfo, @RequestParam String csrf, HttpServletRequest req, HttpSession session) {
        CompletableFuture<ModelAndView> future = new CompletableFuture<>();
        ModelAndView mav = new ModelAndView();
        if (csrf == null || !csrf.equals(session.getAttribute("csrf_token"))) {
            String msg = "This is not a valid request";
            mav.setViewName("common/Error");
            future.complete(mav);
            return future;
        }
        Order sessionOrder = (Order) session.getAttribute("order");
        if (shippingAddressRequired) {
            changeBillInfo(sessionOrder, order);
            session.setAttribute("order", sessionOrder);
            mav.setViewName("order/ShippingForm");
            future.complete(mav);
            return future;
        } else if(!confirmed) {
            if (changeShipInfo)
                changeShipInfo(sessionOrder, order);
            session.setAttribute("order", sessionOrder);
            mav.setViewName("order/ConfirmOrder");
            future.complete(mav);
            return future;
        } else if (order != null) {
            int stat = orderService.insertOrder(sessionOrder);

            if (stat == 1) {
                session.removeAttribute("cart");

                String msg = "Thank you, your order has been submitted.";
                mav.setViewName("order/ViewOrder");
                mav.addObject("msg", msg);
                future.complete(mav);
                return future;
            } else if (stat == 0) {
                // 주문 실패 시 결제 직전으로 되돌아감
                mav.setViewName("order/ConfirmOrder");
                future.complete(mav);
                return future;
            } else {
                // 수량 감소 확인 재요청 실패, 빠져나와서 비동기로 새로운 트랜잭션을 수행
                // 요청을 큐에 저장, 추후 처리 메시지를 받았을 때 다시 처리하게 될 요청임
                int uuid = sessionOrder.getOrderId();
                asyncRequests.put(uuid, Pair.of(future, sessionOrder));
                return future;
            }
        } else {
            String msg = "An error occurred processing your order (order was null).";
            mav.setViewName("common/Error");
            mav.addObject("msg", msg);
            future.complete(mav);
            return future;
        }
    }

    @GetMapping("/viewOrder")
    public String viewOrder(@RequestParam int orderId, HttpServletRequest req, HttpSession session, RedirectAttributes redirect) {
        Account account = (Account) session.getAttribute("account");

        if (account == null) {
            String msg = "You must sign on before attempting to check out.  Please sign on and try checking out again.";
            redirect.addAttribute("msg", msg);
            return "redirect:" + REDIRECT_BASE_URL + "/account/signonForm";
        }
        Order order = orderService.getOrder(orderId);
        if (account.getUsername().equals(order.getUsername())) {
            req.setAttribute("order", order);
            return "order/ViewOrder";
        } else {
            String msg = "You may only view your own orders.";
            req.setAttribute("msg", msg);
            return "common/Error";
        }
    }

    public void changeBillInfo(Order sessionOrder, Order order) {
        sessionOrder.setCardType(order.getCardType());
        sessionOrder.setCreditCard(order.getCreditCard());
        sessionOrder.setExpiryDate(order.getExpiryDate());
        sessionOrder.setBillToFirstName(order.getBillToFirstName());
        sessionOrder.setBillToLastName(order.getBillToLastName());
        sessionOrder.setBillAddress1(order.getBillAddress1());
        sessionOrder.setBillAddress2(order.getBillAddress2());
        sessionOrder.setBillCity(order.getBillCity());
        sessionOrder.setBillState(order.getBillState());
        sessionOrder.setBillZip(order.getBillZip());
        sessionOrder.setBillCountry(order.getBillCountry());
    }

    public void changeShipInfo(Order sessionOrder, Order order) {
        sessionOrder.setShipToFirstName(order.getShipToFirstName());
        sessionOrder.setShipToLastName(order.getShipToLastName());
        sessionOrder.setShipAddress1(order.getShipAddress1());
        sessionOrder.setShipAddress2(order.getShipAddress2());
        sessionOrder.setShipCity(order.getShipCity());
        sessionOrder.setShipState(order.getShipState());
        sessionOrder.setShipZip(order.getShipZip());
        sessionOrder.setShipCountry(order.getShipCountry());
    }

    @KafkaListener(topics="pending_order", groupId = "group_1")
    public CompletableFuture<ModelAndView> reInsertOrder(PendingOrder data) {
        Pair<CompletableFuture<ModelAndView>, Order> pair = asyncRequests.get(data.getOrderId());
        asyncRequests.remove(data.getOrderId());

        CompletableFuture<ModelAndView> future = pair.getFirst();
        Order order = pair.getSecond();
        ModelAndView mav = new ModelAndView();

        int stat = orderService.reInsertOrder(order);

        if (stat == 1) {
            // 주문 성공
            // 세션 카트를 비우기 위해서 redirect 전송
            mav.setViewName("redirect:" + REDIRECT_BASE_URL + "/order/redirectOrder");
            future.complete(mav);
            return future;
        } else {
            mav.setViewName("order/ConfirmOrder");
            future.complete(mav);
            return future;
        }
    }

    @GetMapping("/redirectOrder")
    public String redirectOrder(HttpSession session, HttpServletRequest req) {
        session.removeAttribute("cart");

        String msg = "Thank you, your order has been submitted.";
        req.setAttribute("msg", msg);
        return "order/ViewOrder";
    }
}
