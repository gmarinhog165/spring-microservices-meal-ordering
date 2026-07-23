package com.example.bookingservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingEvent {
    private Long user_id;
    private Long restaurant_id;
    private Map<Long, Integer> productQuantities;
    private BigDecimal totalPrice;
}