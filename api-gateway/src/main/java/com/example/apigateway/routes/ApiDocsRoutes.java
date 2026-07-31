package com.example.apigateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.setPath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class ApiDocsRoutes {

    @Value("${services.booking.url}")
    private String bookingUrl;

    @Value("${services.inventory.url}")
    private String inventoryUrl;

    @Value("${services.auth.url}")
    private String authUrl;

    @Bean
    public RouterFunction<ServerResponse> bookingApiDocsRoute() {
        return route("booking-service-docs")
                .route(RequestPredicates.path("/api-docs/booking/**"), http())
                .before(uri(bookingUrl))
                .before(setPath("/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryApiDocsRoute() {
        return route("inventory-service-docs")
                .route(RequestPredicates.path("/api-docs/inventory/**"), http())
                .before(uri(inventoryUrl))
                .before(setPath("/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authApiDocsRoute() {
        return route("auth-service-docs")
                .route(RequestPredicates.path("/api-docs/auth/**"), http())
                .before(uri(authUrl))
                .before(setPath("/v3/api-docs"))
                .build();
    }
}