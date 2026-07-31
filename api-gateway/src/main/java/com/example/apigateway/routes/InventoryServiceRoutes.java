package com.example.apigateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class InventoryServiceRoutes {

    @Value("${services.inventory.url}")
    private String inventoryUrl;

    @Bean
    public RouterFunction<ServerResponse> inventoryRoutes() {
        return route("inventory-service")
                .route(RequestPredicates.path("api/v1/inventory/**"), http())
                .before(uri(inventoryUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("inventory-circuit-breaker", URI.create("forward:/fallbackRoute/inventory")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryFallbackRoute(){
        return route("inventory-service-fallback")
                .route(RequestPredicates.path("/fallbackRoute/inventory"),
        request-> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Inventory Service is down."))
                .build();
    }

    // Caso queira alterar o path do api gateway:
//    @Bean
//    public RouterFunction<ServerResponse> inventoryRoutes() {
//        return route("inventory-service")
//                .route(RequestPredicates.path("/inventory/**"), http())
//                .before(uri(inventoryUrl))
//                .before(rewritePath("/inventory/(?<segment>.*)", "/api/v1/inventory/${segment}"))
//                .build();
//    }
}
