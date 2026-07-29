package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.MenuItem;
import com.example.inventoryservice.entity.Restaurant;
import com.example.inventoryservice.repository.InventoryRepository;
import com.example.inventoryservice.repository.MenuItemRepository;
import com.example.inventoryservice.response.MenuInventoryResponse;
import com.example.inventoryservice.response.RestaurantInventoryResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class InventoryService {
    private InventoryRepository inventoryRepository;
    private MenuItemRepository menuItemRepository;

    @Autowired
    public InventoryService(InventoryRepository inventoryRepository, MenuItemRepository menuItemRepository){
        this.inventoryRepository = inventoryRepository;
        this.menuItemRepository = menuItemRepository;
    }

    public List<RestaurantInventoryResponse> getAllRestaurants(){
        List<Restaurant> restaurants = inventoryRepository.findAll();
        return restaurants.stream()
                .map(restaurant -> RestaurantInventoryResponse.builder()
                        .id(restaurant.getId())
                        .name(restaurant.getName())
                        .address(restaurant.getAddress())
                        .build())
                .toList();
    }

    public List<MenuInventoryResponse> getRestaurantMenu(Long restaurantId){
        Restaurant restaurant = inventoryRepository.findById(restaurantId).orElse(null);
        if(restaurant == null) {
            throw new IllegalArgumentException("Restaurant not found");
        }
        List<MenuItem> menuItems = restaurant.getMenu();
        return menuItems.stream()
                .map(item -> MenuInventoryResponse.builder()
                        .id(item.getId())
                        .name(item.getName())
                        .price(item.getPrice())
                        .build())
                .toList();
    }

    public MenuItem getMenuItem(Long menuItemId) {
        return menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new EntityNotFoundException("Menu item not found: " + menuItemId));
    }

    @Transactional
    public void decrementMenuItemQuantity(Long menuItemId, int ordered) {
        if (ordered <= 0) {
            throw new IllegalArgumentException("Ordered amount must be positive");
        }
        if (!menuItemRepository.existsById(menuItemId)) {
            throw new EntityNotFoundException("Menu item not found: " + menuItemId);
        }
        if (menuItemRepository.decrementQuantity(menuItemId, ordered) == 0) {
            throw new IllegalStateException("Insufficient stock for menu item " + menuItemId);
        }
        log.info("Decremented quantity for menu item ID: {} by {}", menuItemId, ordered);
    }
}