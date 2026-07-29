package com.example.bookingservice.client;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcChannelConfig {

    @Bean(destroyMethod = "shutdown")
    public ManagedChannel inventoryChannel(
            @Value("${inventory.service.address}") String address,
            @Value("${inventory.service.grpc.port}") int port) {

        return ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();
    }
}