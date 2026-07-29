package com.example.orderservice.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcChannelConfig {

    // O canal e' gerido pelo Spring para garantir que e' fechado no shutdown;
    // e' thread-safe e deve ser partilhado por todos os stubs.
    @Bean(destroyMethod = "shutdown")
    public ManagedChannel inventoryChannel(
            @Value("${inventory.service.address}") String address,
            @Value("${inventory.service.grpc.port}") int port) {

        return ManagedChannelBuilder.forAddress(address, port)
                .usePlaintext()
                .build();
    }
}