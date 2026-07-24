package com.example.auth_service.client;

import com.example.auth_service.response.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class KeycloakClient {

    @Value("${keycloak.token-uri}")
    private String tokenUri;

    @Value("${keycloak.admin-users-uri}")
    private String adminUsersUri;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();

    public UUID createUser(String email, String password, String name) {
        String adminToken = getAdminToken();

        String[] nameParts = name.trim().split("\\s+", 2);
        String firstName = nameParts[0];
        String lastName = nameParts.length > 1 ? nameParts[1] : nameParts[0];

        Map<String, Object> credential = Map.of(
                "type", "password",
                "value", password,
                "temporary", false
        );
        Map<String, Object> userRepresentation = Map.of(
                "username", email,
                "email", email,
                "firstName", firstName,
                "lastName", lastName,
                "enabled", true,
                "emailVerified", true,
                "requiredActions", List.of(),
                "credentials", List.of(credential)
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Void> response = restTemplate.postForEntity(
                    adminUsersUri, new HttpEntity<>(userRepresentation, headers), Void.class);
            return extractUserId(response);
        } catch (HttpClientErrorException.Conflict e) {
            throw new IllegalStateException("Email already registered");
        }
    }

    public void deleteUser(UUID keycloakId) {
        String adminToken = getAdminToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        restTemplate.exchange(adminUsersUri + "/" + keycloakId, HttpMethod.DELETE,
                new HttpEntity<>(headers), Void.class);
    }

    public LoginResponse login(String email, String password) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", email);
        body.add("password", password);

        try {
            return requestToken(body);
        } catch (HttpClientErrorException.Unauthorized e) {
            throw new IllegalArgumentException("Invalid email or password");
        }
    }

    private String getAdminToken() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        return requestToken(body).getAccessToken();
    }

    private LoginResponse requestToken(MultiValueMap<String, String> body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return restTemplate.postForObject(tokenUri, new HttpEntity<>(body, headers), LoginResponse.class);
    }

    private UUID extractUserId(ResponseEntity<Void> response) {
        URI location = response.getHeaders().getLocation();
        if (location == null) {
            throw new IllegalStateException("Keycloak did not return the created user location");
        }
        String path = location.getPath();
        return UUID.fromString(path.substring(path.lastIndexOf('/') + 1));
    }
}
