package com.example.inventoryservice.repository;

import com.example.inventoryservice.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

    @Modifying
    @Query("UPDATE MenuItem m SET m.quantity = m.quantity - :ordered WHERE m.id = :id AND m.quantity >= :ordered")
    int decrementQuantity(@Param("id") Long id, @Param("ordered") Integer ordered);
}
