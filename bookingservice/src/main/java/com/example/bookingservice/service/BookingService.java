package com.example.bookingservice.service;

import com.example.bookingservice.client.InventoryServiceClient;
import com.example.bookingservice.entity.Customer;
import com.example.bookingservice.event.BookingEvent;
import com.example.bookingservice.repository.CustomerRepository;
import com.example.bookingservice.request.BookingRequest;
import com.example.bookingservice.response.InventoryResponse;
import com.example.bookingservice.response.ReceiptResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BookingService {
    private CustomerRepository customerRepository;
    private InventoryServiceClient inventoryServiceClient;
    private KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @Autowired
    public BookingService(CustomerRepository bookingRepository, InventoryServiceClient inventoryServiceClient, KafkaTemplate<String, BookingEvent> kafkaTemplate) {
        this.customerRepository = bookingRepository;
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ReceiptResponse createBooking(BookingRequest request){
        log.info("Creating booking for customer: {}", request.getUser_id());
        // check user exists
        Customer customer = customerRepository.findById(request.getUser_id()).orElse(null);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        // check there is enough quantity for the order
        log.info("Checking stock for products: {}", request.getProductQuantities());
        Map<Long, Integer> productQuantities = request.getProductQuantities();
        BigDecimal total = BigDecimal.valueOf(0);
        Map<String, Integer> receiptProductQuantities = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();
            InventoryResponse quantityResponse = inventoryServiceClient.getStock(itemId);
            if (quantityResponse.getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock available for item: " + itemId);
            }
            total = total.add(quantityResponse.getPrice().multiply(BigDecimal.valueOf(quantity)));
            receiptProductQuantities.put(quantityResponse.getName(), quantity);
            log.info("Stock available for item: {}, quantity: {}", itemId, quantity);
        }

        // create booking
        log.info("Booking created for customer: {}, total price: {}", request.getUser_id(), total);
        BookingEvent bookingEvent = BookingEvent.builder()
                .user_id(request.getUser_id())
                .restaurant_id(request.getRestaurant_id())
                .productQuantities(request.getProductQuantities())
                .totalPrice(total)
                .build();
        log.info("Sending booking event to order service: {}", bookingEvent);
        // send booking to order service through kafka
        kafkaTemplate.send("booking-events", bookingEvent);
        log.info("Booking event sent to order service: {}", bookingEvent);

        return ReceiptResponse.builder()
                .user_id(request.getUser_id())
                .restaurant_id(request.getRestaurant_id())
                .productQuantities(receiptProductQuantities)
                .totalPrice(total)
                .build();
    }


}
