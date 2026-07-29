package com.example.bookingservice.service;

import com.example.bookingservice.client.InventoryServiceGrpcClient;
import com.example.bookingservice.event.BookingEvent;
import com.example.bookingservice.request.BookingRequest;
import com.example.bookingservice.response.ReceiptResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class BookingService {
    private InventoryServiceGrpcClient inventoryServiceClient;
    private KafkaTemplate<String, BookingEvent> kafkaTemplate;

    @Autowired
    public BookingService(InventoryServiceGrpcClient inventoryServiceClient, KafkaTemplate<String, BookingEvent> kafkaTemplate) {
        this.inventoryServiceClient = inventoryServiceClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ReceiptResponse createBooking(BookingRequest request){
        log.info("Creating booking for customer: {}", request.getUser_id());
        // check user exists: removed
        // check there is enough quantity for the order
        log.info("Checking stock for products: {}", request.getProductQuantities());
        Map<Long, Integer> productQuantities = request.getProductQuantities();
        BigDecimal total = BigDecimal.valueOf(0);
        Map<String, Integer> receiptProductQuantities = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : productQuantities.entrySet()) {
            Long itemId = entry.getKey();
            Integer quantity = entry.getValue();
            var quantityResponse = inventoryServiceClient.getInventory(itemId);
            log.info("Retrieved inventory for item: {}, quantity: {}", itemId, quantityResponse.getQuantity());
            if (quantityResponse.getQuantity() < quantity) {
                throw new IllegalArgumentException("Not enough stock available for item: " + itemId);
            }
            total = total.add(new BigDecimal(quantityResponse.getPrice()).multiply(BigDecimal.valueOf(quantity)));
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
        // send booking to order service through kafka; o send e' assincrono,
        // so' sabemos que foi mesmo publicado quando o broker confirma
        kafkaTemplate.send("booking-events", bookingEvent)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to send booking event: {}", bookingEvent, ex);
                        return;
                    }
                    RecordMetadata metadata = result.getRecordMetadata();
                    log.info("Booking event sent to order service: {} (topic={}, partition={}, offset={})",
                            bookingEvent, metadata.topic(), metadata.partition(), metadata.offset());
                });

        return ReceiptResponse.builder()
                .user_id(request.getUser_id())
                .restaurant_id(request.getRestaurant_id())
                .productQuantities(receiptProductQuantities)
                .totalPrice(total)
                .build();
    }


}
