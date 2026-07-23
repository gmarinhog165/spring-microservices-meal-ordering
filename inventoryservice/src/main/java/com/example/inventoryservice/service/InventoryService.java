package com.example.inventoryservice.service;

import com.example.inventoryservice.entity.MenuItem;
import com.example.inventoryservice.entity.Restaurant;
import com.example.inventoryservice.repository.InventoryRepository;
import com.example.inventoryservice.repository.MenuItemRepository;
import com.example.inventoryservice.response.MenuInventoryResponse;
import com.example.inventoryservice.response.RestaurantInventoryResponse;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public MenuInventoryResponse getMenuItemInventory(Long menuItemId) {
        MenuItem menuItem = menuItemRepository.findById(menuItemId).orElse(null);
        if (menuItem == null) {
            throw new IllegalArgumentException("Menu item not found");
        }
        return MenuInventoryResponse.builder()
                .quantity(menuItem.getQuantity())
                .build();
    }

    @Transactional
    public void updateMenuItemQuantity(Long menuItemId, Integer ordered) {
        if (ordered == null || ordered <= 0) {
            throw new IllegalArgumentException("Ordered amount must be positive");
        }
        if (!menuItemRepository.existsById(menuItemId)) {
            throw new IllegalArgumentException("Menu item not found");
        }
        int updatedRows = menuItemRepository.decrementQuantity(menuItemId, ordered);
        if (updatedRows == 0) {
            throw new IllegalStateException("Insufficient stock for menu item ID: " + menuItemId);
        }
        log.info("Decremented quantity for menu item ID: {} by {}", menuItemId, ordered);
    }
}