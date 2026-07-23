package com.example.inventoryservice.repository;

import com.example.inventoryservice.entity.MenuItem;
import com.example.inventoryservice.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Restaurant, Long> {
}
