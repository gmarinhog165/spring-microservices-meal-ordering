package com.example.auth_service.client;

import com.example.auth_service.request.CustomerRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CustomerServiceClient {

    @Value("${customer.service.url}")
    private String customerServiceUrl;

    public Void createCustomer(CustomerRequest request) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.postForObject(customerServiceUrl, request, Void.class);
    }
}
