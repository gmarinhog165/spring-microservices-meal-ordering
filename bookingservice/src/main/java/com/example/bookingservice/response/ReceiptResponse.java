package com.example.bookingservice.response;

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
public class ReceiptResponse {
    private Long user_id;
    private BigDecimal totalPrice;
    private Long restaurant_id;
    private Map<String, Integer> productQuantities;
}
