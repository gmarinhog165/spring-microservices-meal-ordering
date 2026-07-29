package com.example.bookingservice.client;

import com.example.inventory.GetInventoryRequest;
import com.example.inventory.GetInventoryResponse;
import com.example.inventory.InventoryServiceGrpc;
import io.grpc.ManagedChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class InventoryServiceGrpcClient {

    private static final long DEADLINE_SECONDS = 5;

    private final InventoryServiceGrpc.InventoryServiceBlockingStub blockingStub;

    @Autowired
    public InventoryServiceGrpcClient(ManagedChannel inventoryChannel) {
        this.blockingStub = InventoryServiceGrpc.newBlockingStub(inventoryChannel);
    }

    public GetInventoryResponse getInventory(Long productId) {
        GetInventoryRequest request = GetInventoryRequest.newBuilder()
                .setProductId(productId)
                .build();

        return blockingStub.withDeadlineAfter(DEADLINE_SECONDS, TimeUnit.SECONDS)
                .getInventory(request);
    }
}
