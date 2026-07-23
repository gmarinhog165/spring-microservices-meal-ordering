package com.example.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "menu_item")
@Entity
public class MenuItem {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;
    private int quantity;
    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;
}
