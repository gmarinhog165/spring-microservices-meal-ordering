package com.example.orderservice.listener;

import com.example.bookingservice.event.BookingEvent;
import com.example.orderservice.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BookingEventListener {

    private OrderService orderService;

    @Autowired
    public BookingEventListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "booking-events", groupId = "orderservice")
    public void onBookingEvent(BookingEvent bookingEvent) {
        log.info("Received booking event: {}", bookingEvent);
        orderService.createOrderFromEvent(bookingEvent);
    }
}
