package com.example.apigateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.filter.FilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
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
public class BookingServiceRoutes {

    @Value("${services.booking.url}")
    private String bookingUrl;

    @Bean
    public RouterFunction<ServerResponse> bookingRoutes() {
        return route("booking-service")
                .route(RequestPredicates.path("api/v1/booking/**"), http())
                .before(uri(bookingUrl))
                .filter(CircuitBreakerFilterFunctions.circuitBreaker("booking-circuit-breaker", URI.create("forward:/fallbackRoute/booking")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> bookingFallbackRoute(){
        return route("booking-service-fallback")
                .route(RequestPredicates.path("/fallbackRoute/booking"),
                        request-> ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE)
                                .body("Booking Service is down."))
                .build();
    }
}
