package com.example.inventoryservice.grpc;

import com.example.inventory.InventoryServiceGrpc;
import com.example.inventory.UpdateInventoryRequest;
import com.example.inventoryservice.repository.MenuItemRepository;
import com.google.protobuf.Empty;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.transaction.annotation.Transactional;

@GrpcService
@Slf4j
public class InventoryGrpcService extends InventoryServiceGrpc.InventoryServiceImplBase {

    private final MenuItemRepository menuItemRepository;

    public InventoryGrpcService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    @Transactional
    public void updateInventory(UpdateInventoryRequest request, StreamObserver<Empty> responseObserver) {
        log.info("Updating inventory for product ID: {}", request.getProductId());

        long menuItemId = Long.parseLong(request.getProductId());
        int ordered = Integer.parseInt(request.getQuantity());

        if (ordered <= 0) {
            throw new IllegalArgumentException("Ordered amount must be positive");
        }
        if (!menuItemRepository.existsById(menuItemId)) {
            throw new EntityNotFoundException("Menu item not found: " + menuItemId);
        }
        if (menuItemRepository.decrementQuantity(menuItemId, ordered) == 0) {
            throw new IllegalStateException("Insufficient stock for menu item " + menuItemId);
        }

        log.info("Decremented quantity for menu item ID: {} by {}", menuItemId, ordered);

        responseObserver.onNext(Empty.getDefaultInstance());
        responseObserver.onCompleted();
    }
}