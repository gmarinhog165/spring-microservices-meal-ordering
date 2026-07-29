package com.example.inventoryservice.grpc;

import com.example.inventory.GetInventoryRequest;
import com.example.inventory.GetInventoryResponse;
import com.example.inventory.InventoryServiceGrpc;
import com.example.inventory.UpdateInventoryRequest;
import com.example.inventoryservice.entity.MenuItem;
import com.example.inventoryservice.service.InventoryService;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
@Slf4j
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final InventoryService inventoryService;

    public InventoryGrpcService(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @Override
    public void updateInventory(UpdateInventoryRequest request, StreamObserver<Empty> responseObserver) {
        log.info("gRPC UpdateInventory for menu item ID: {}", request.getProductId());

        inventoryService.decrementMenuItemQuantity(request.getProductId(), request.getQuantity());

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }

    @Override
    public void getInventory(GetInventoryRequest request, StreamObserver<GetInventoryResponse> responseObserver) {
        log.info("gRPC GetInventory for menu item ID: {}", request.getProductId());

        MenuItem menuItem = inventoryService.getMenuItem(request.getProductId());

        responseObserver.onNext(toResponse(menuItem));
        responseObserver.onCompleted();
    }

    private GetInventoryResponse toResponse(MenuItem menuItem) {
        return GetInventoryResponse.newBuilder()
                .setProductId(menuItem.getId())
                .setQuantity(menuItem.getQuantity())
                .setName(menuItem.getName())
                .setPrice(menuItem.getPrice().toPlainString())
                .build();
    }
}