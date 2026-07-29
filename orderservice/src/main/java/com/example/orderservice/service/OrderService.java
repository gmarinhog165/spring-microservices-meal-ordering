package com.example.orderservice.service;

import com.example.bookingservice.event.BookingEvent;
import com.example.orderservice.entity.Order;
import com.example.orderservice.grpc.InventoryServiceGrpcClient;
import com.example.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryServiceGrpcClient inventoryServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, InventoryServiceGrpcClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.orderRepository = orderRepository;
    }

    public void createOrderFromEvent(BookingEvent bookingEvent) {
        log.info("Creating order from booking event: {}", bookingEvent);

        // O inventario e' decrementado antes de persistir a order: se algum item
        // nao tiver stock a chamada gRPC falha e nao fica nenhuma order gravada.
        for (Map.Entry<Long, Integer> entry : bookingEvent.getProductQuantities().entrySet()) {
            inventoryServiceClient.updateInventory(entry.getKey(), entry.getValue());
        }

        Order order = Order.builder()
                .total_price(bookingEvent.getTotalPrice())
                .placedAt(LocalDateTime.now())
                .user_id(bookingEvent.getUser_id())
                .restaurant_id(bookingEvent.getRestaurant_id())
                .product_quantities(bookingEvent.getProductQuantities())
                .build();
        orderRepository.save(order);
        log.info("Order created successfully: {}", order);
    }
}
