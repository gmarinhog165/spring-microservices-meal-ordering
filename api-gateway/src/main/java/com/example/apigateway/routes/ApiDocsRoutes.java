package com.example.apigateway.routes;

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

    @Bean
    public RouterFunction<ServerResponse> bookingApiDocsRoute() {
        return route("booking-service-docs")
                .route(RequestPredicates.path("/api-docs/booking/**"), http())
                .before(uri("http://localhost:8081"))
                .before(setPath("/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryApiDocsRoute() {
        return route("inventory-service-docs")
                .route(RequestPredicates.path("/api-docs/inventory/**"), http())
                .before(uri("http://localhost:8080"))
                .before(setPath("/v3/api-docs"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authApiDocsRoute() {
        return route("auth-service-docs")
                .route(RequestPredicates.path("/api-docs/auth/**"), http())
                .before(uri("http://localhost:8086"))
                .before(setPath("/v3/api-docs"))
                .build();
    }
}