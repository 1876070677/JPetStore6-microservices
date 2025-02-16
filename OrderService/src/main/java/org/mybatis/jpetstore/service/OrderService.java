/*
 *    Copyright 2010-2022 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.jpetstore.service;

import org.mybatis.jpetstore.DTO.PendingOrder;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.Order;
import org.mybatis.jpetstore.domain.Sequence;
import org.mybatis.jpetstore.http.HttpFacade;
import org.mybatis.jpetstore.mapper.LineItemMapper;
import org.mybatis.jpetstore.mapper.OrderMapper;
import org.mybatis.jpetstore.mapper.SequenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Class OrderService.
 *
 * @author Eduardo Macarron
 */
@Service
public class OrderService {

  private final OrderMapper orderMapper;
  private final SequenceMapper sequenceMapper;
  private final LineItemMapper lineItemMapper;
  private final HttpFacade httpFacade;
  private final KafkaTemplate<String, Object> kafkaTemplate;

  @Autowired
  public OrderService(OrderMapper orderMapper, SequenceMapper sequenceMapper,
                      LineItemMapper lineItemMapper, HttpFacade httpFacade, KafkaTemplate<String, Object> kafkaTemplate) {
    this.orderMapper = orderMapper;
    this.sequenceMapper = sequenceMapper;
    this.lineItemMapper = lineItemMapper;
    this.httpFacade = httpFacade;
    this.kafkaTemplate = kafkaTemplate;
  }

  /**
   * Insert order.
   *
   * @param order
   *          the order
   */
  @Transactional
  public int insertOrder(Order order) {
    /*
      정상 실패: 0
      성공: 1
      비정상 실패: 1
     */
    // OrderId 가져오면서 락걸기
    order.setOrderId(getNextId("ordernum"));
    Map<String, Object> param = new HashMap<>();
    order.getLineItems().forEach(lineItem -> {
      String itemId = lineItem.getItemId();
      Integer increment = lineItem.getQuantity();
      param.put(itemId, increment);
    });
    int resp = -1;
    try {
      // http 통신을 id마다 하지말고, 한번에 할 것
      resp = httpFacade.updateInventoryQuantity(param, order.getOrderId());
      if (resp == 0 || resp == 2) {
        // 변경 요청이 실패한 경우 (재요청 포함) 트랜잭션을 실패로
        throw new Exception("Change Quantity Failed");
      }
    } catch (Exception e) {
      // 트랜잭션을 실패로 마킹 및 함수 종료
      if (resp == 2) {
        // 비정상 실패의 경우, 지연 시간 메시지를 발행
        PendingOrder po = new PendingOrder(order.getOrderId(), 1);
        kafkaTemplate.send("pending_order_schedule", po);
      }
      return resp;
    }

    try {
      orderMapper.insertOrder(order);
      orderMapper.insertOrderStatus(order);
      order.getLineItems().forEach(lineItem -> {
        lineItem.setOrderId(order.getOrderId());
        lineItemMapper.insertLineItem(lineItem);
      });
    } catch(Exception e) {
      // 주문 오류 시 보상 트랜잭션 메시지 발행
      kafkaTemplate.send("prod_compensation", param);
      return 0;
    }
    return 1;
  }

  @Transactional
  public int reInsertOrder(Order order) {
    Map<String, Object> param = new HashMap<>();
    order.getLineItems().forEach(lineItem -> {
      String itemId = lineItem.getItemId();
      Integer increment = lineItem.getQuantity();
      param.put(itemId, increment);
    });
    int resp = -1;
    try {
      // http 통신을 id마다 하지말고, 한번에 할 것
      resp = httpFacade.checkChangeQuantity(order.getOrderId());
      if (resp == 0 || resp == 2) {
        // 변경 요청이 실패한 경우 (재요청 포함) 트랜잭션을 실패로
        throw new Exception("Change Quantity Failed");
      }
    } catch (Exception e) {
      // 트랜잭션을 실패로 마킹 및 함수 종료
      return resp;
    }

    try {
      orderMapper.insertOrder(order);
      orderMapper.insertOrderStatus(order);
      order.getLineItems().forEach(lineItem -> {
        lineItem.setOrderId(order.getOrderId());
        lineItemMapper.insertLineItem(lineItem);
      });
    } catch(Exception e) {
      // 주문 오류 시 보상 트랜잭션 메시지 발행
      kafkaTemplate.send("prod_compensation", param);
      return 0;
    }
    return 1;
  }

  /**
   * Gets the order.
   *
   * @param orderId
   *          the order id
   *
   * @return the order
   */
  @Transactional
  public Order getOrder(int orderId) {
    Order order = orderMapper.getOrder(orderId);
    order.setLineItems(lineItemMapper.getLineItemsByOrderId(orderId));

    order.getLineItems().forEach(lineItem -> {
      Item item = httpFacade.getItem(lineItem.getItemId());
      item.setQuantity(httpFacade.getInventoryQuantity(lineItem.getItemId()));
      lineItem.setItem(item);
    });

    return order;
  }

  /**
   * Gets the orders by username.
   *
   * @param username
   *          the username
   *
   * @return the orders by username
   */
  public List<Order> getOrdersByUsername(String username) {
    return orderMapper.getOrdersByUsername(username);
  }

  /**
   * Gets the next id.
   *
   * @param name
   *          the name
   *
   * @return the next id
   */
  public int getNextId(String name) {
    Sequence sequence = sequenceMapper.getSequence(new Sequence(name, -1));
    if (sequence == null) {
      throw new RuntimeException(
          "Error: A null sequence was returned from the database (could not get next " + name + " sequence).");
    }
    Sequence parameterObject = new Sequence(name, sequence.getNextId() + 1);
    sequenceMapper.updateSequence(parameterObject);
    return sequence.getNextId();
  }

}
