package com.example.orderservice.grpc;

import com.example.inventory.InventoryServiceGrpc;
import com.example.inventory.UpdateInventoryRequest;
import io.grpc.ManagedChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class InventoryServiceGrpcClient {

    private static final long DEADLINE_SECONDS = 5;

    private final InventoryServiceGrpc.InventoryServiceBlockingStub blockingStub;

    public InventoryServiceGrpcClient(ManagedChannel inventoryChannel) {
        this.blockingStub = InventoryServiceGrpc.newBlockingStub(inventoryChannel);
    }

    public void updateInventory(Long productId, int quantity) {
        log.info("gRPC UpdateInventory for menu item ID: {}, quantity: {}", productId, quantity);

        UpdateInventoryRequest request = UpdateInventoryRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build();

        // O deadline e' absoluto, por isso tem de ser aplicado por chamada:
        // se ficasse no stub expirava de vez passados DEADLINE_SECONDS.
        blockingStub.withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                .updateInventory(request);
    }
}