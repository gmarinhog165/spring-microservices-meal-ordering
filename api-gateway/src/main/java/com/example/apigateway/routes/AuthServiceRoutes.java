package com.example.apigateway.routes;

import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import java.net.URI;

import static org.springframework.cloud.gateway.server.mvc.filter.BeforeFilterFunctions.uri;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class AuthServiceRoutes {

    @Bean
    public RouterFunction<ServerResponse> authRoutes() {
        return route("auth-service")
                .route(RequestPredicates.path("api/v1/auth/**"), http())
                .before(uri("http://localhost:8086"))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("auth-circuit-breaker", URI.create("forward:/fallbackRoute/auth")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> authFallbackRoute(){
        return route("auth-service-fallback")
                .route(RequestPredicates.path("/fallbackRoute/auth"),
                        request-> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Auth Service is down."))
                .build();
    }
}
