package com.example.orderservice.service;

import com.example.bookingservice.event.BookingEvent;
import com.example.orderservice.client.InventoryServiceClient;
import com.example.orderservice.entity.Order;
import com.example.orderservice.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class OrderService {

    private OrderRepository orderRepository;
    private InventoryServiceClient inventoryServiceClient;

    @Autowired
    public OrderService(OrderRepository orderRepository, InventoryServiceClient inventoryServiceClient) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.orderRepository = orderRepository;
    }

    public void createOrderFromEvent(BookingEvent bookingEvent) {
        log.info("Creating order from booking event: {}", bookingEvent);
        Order order = Order.builder()
                .total_price(bookingEvent.getTotalPrice())
                .placedAt(LocalDateTime.now())
                .user_id(bookingEvent.getUser_id())
                .restaurant_id(bookingEvent.getRestaurant_id())
                .product_quantities(bookingEvent.getProductQuantities())
                .build();
        log.info("Order created successfully: {}", order);
        orderRepository.save(order);

        // Update inventory for each product in the order
        for (Map.Entry<Long, Integer> entry : bookingEvent.getProductQuantities().entrySet()) {
            log.info("Updating inventory for item_id: {}, quantity: {}", entry.getKey(), entry.getValue());
            inventoryServiceClient.updateInventory(entry.getKey(), entry.getValue());
        }
    }
}
