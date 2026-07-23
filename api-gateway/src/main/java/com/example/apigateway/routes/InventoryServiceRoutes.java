package com.example.apigateway.routes;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.rewritePath;
import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class InventoryServiceRoutes {

    @Bean
    public RouterFunction<ServerResponse> inventoryRoutes() {
        return route("inventory-service")
                .route(RequestPredicates.path("api/v1/inventory/**"), http())
                .before(uri("http://localhost:8080"))
                .build();
    }

    // Caso queira alterar o path do api gateway:
//    @Bean
//    public RouterFunction<ServerResponse> inventoryRoutes() {
//        return route("inventory-service")
//                .route(RequestPredicates.path("/inventory/**"), http())
//                .before(uri("http://localhost:8080"))
//                .before(rewritePath("/inventory/(?<segment>.*)", "/api/v1/inventory/${segment}"))
//                .build();
//    }
}
