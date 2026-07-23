package com.example.orderservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class InventoryServiceClient {

    @Value("${inventory.service.url}")
    private String inventoryServiceUrl;

    public ResponseEntity<Void> updateInventory(Long item_id, int quantity){
        RestTemplate restTemplate = new RestTemplate();
        QuantityRequest request = QuantityRequest.builder().quantity(quantity).build();
        restTemplate.put(inventoryServiceUrl + "/quantity/" + item_id, request);
        return ResponseEntity.noContent().build();
    }
}
