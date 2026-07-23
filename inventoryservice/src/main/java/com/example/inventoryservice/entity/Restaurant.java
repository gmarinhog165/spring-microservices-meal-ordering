package com.example.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "restaurant")
@Entity
public class Restaurant {
    @Id
    private Long id;
    private String address;
    private String name;
    private boolean open;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menu = new ArrayList<>();
}
