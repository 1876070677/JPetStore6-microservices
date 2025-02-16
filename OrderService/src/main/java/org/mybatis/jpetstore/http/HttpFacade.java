package org.mybatis.jpetstore.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mybatis.jpetstore.domain.Item;
import org.mybatis.jpetstore.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class HttpFacade {
    @Autowired
    RestTemplate restTemplate;
    ObjectMapper objectMapper;

    private static final String CATALOG_SERVICE_URL = "http://localhost:8080/catalog";

    public int updateInventoryQuantity(Map<String, Object> param, int uuid) {
        String quantityString = param.values().stream().map(String::valueOf).collect(Collectors.joining(","));
        // orderId를 uuid로 사용
        String url = CATALOG_SERVICE_URL + "/updateQuantity?itemId=" + String.join(",", param.keySet()) + "&increment=" + quantityString + "&uuid=" + uuid;

        int responses = -1;
        try {
            ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(url, Boolean.class);
            responses = responseEntity.getBody() == true ? 1 : 0;
        } catch (HttpServerErrorException | ResourceAccessException | HttpClientErrorException e) {
            // 5xx 오류 or Timeout 오류
            // 실제로 처리되었는지 아닌지 확인
            responses = checkChangeQuantity(uuid);
        }

        return responses;
    }

    public int checkChangeQuantity(int uuid) {
        int responses = -1;
        String url = CATALOG_SERVICE_URL + "/checkChangeQuantity?uuid=" + uuid;
        try {
            ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(url, Boolean.class);
            responses = responseEntity.getBody() == true ? 1 : 0;
        } catch (HttpServerErrorException | ResourceAccessException | HttpClientErrorException e2) {
            responses = 2;
        }
        return responses;
    }

    public Item getItem(String itemId) {
        String url = CATALOG_SERVICE_URL + "/getItem?itemId=" + itemId;

        ResponseEntity<Item> responseEntity = restTemplate.getForEntity(url, Item.class);
        Item response = responseEntity.getBody();
        return response;
    }

    public int getInventoryQuantity(String itemId) {
        String url = CATALOG_SERVICE_URL + "/getQuantity?itemId=" + itemId;

        ResponseEntity<Integer> responseEntity = restTemplate.getForEntity(url, Integer.class);
        int response = responseEntity.getBody();
        return response;
    }
}
