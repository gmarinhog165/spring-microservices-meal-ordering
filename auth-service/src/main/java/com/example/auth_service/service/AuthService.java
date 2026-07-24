package com.example.auth_service.service;

import com.example.auth_service.client.CustomerServiceClient;
import com.example.auth_service.client.KeycloakClient;
import com.example.auth_service.request.CustomerRequest;
import com.example.auth_service.request.LoginRequest;
import com.example.auth_service.request.RegisterRequest;
import com.example.auth_service.response.LoginResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AuthService {

    private final KeycloakClient keycloakClient;
    private final CustomerServiceClient customerServiceClient;

    @Autowired
    public AuthService(KeycloakClient keycloakClient, CustomerServiceClient customerServiceClient) {
        this.keycloakClient = keycloakClient;
        this.customerServiceClient = customerServiceClient;
    }

    public void register(RegisterRequest request) {
        log.info("Registering user: {}", request.getEmail());
        UUID keycloakId = keycloakClient.createUser(request.getEmail(), request.getPassword(), request.getName());

        CustomerRequest customerRequest = CustomerRequest.builder()
                .keycloakId(keycloakId)
                .email(request.getEmail())
                .name(request.getName())
                .build();

        try {
            customerServiceClient.createCustomer(customerRequest);
        } catch (Exception e) {
            log.error("Failed to create customer profile for {}, rolling back Keycloak user", request.getEmail(), e);
            keycloakClient.deleteUser(keycloakId);
            throw new IllegalStateException("Failed to complete registration. Try again later.");
        }
    }

    public LoginResponse login(LoginRequest request) {
        return keycloakClient.login(request.getEmail(), request.getPassword());
    }
}
