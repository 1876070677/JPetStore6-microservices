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

    public boolean updateInventoryQuantity(Map<String, Object> param) {
        String quantityString = param.values().stream().map(String::valueOf).collect(Collectors.joining(","));
        // 트랜잭션을 구별하기 위한 uuid 발급
        String uuid = UUID.randomUUID().toString().replace("-", "");
        String url = CATALOG_SERVICE_URL + "/updateQuantity?itemId=" + String.join(",", param.keySet()) + "&increment=" + quantityString + "&uuid=" + uuid;

        boolean responses = false;
        try {
            ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(url, Boolean.class);
            responses = responseEntity.getBody();

        } catch (HttpServerErrorException | ResourceAccessException | HttpClientErrorException e) {
            // 5xx 오류 or Timeout 오류
            // 실제로 처리되었는지 아닌지 확인
            url = CATALOG_SERVICE_URL + "/checkChangeQuantity?uuid=" + uuid;
            try {
                ResponseEntity<Boolean> responseEntity = restTemplate.getForEntity(url, Boolean.class);
                responses = responseEntity.getBody();
            } catch (HttpServerErrorException | ResourceAccessException e2) {
                // Todo: 결과 확인 요청 실패 시 즉시 재요청하지 않고 지연 재요청 구현
            }
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
