package com.example.inventoryservice.controller;

import com.example.inventoryservice.response.MenuInventoryResponse;
import com.example.inventoryservice.response.RestaurantInventoryResponse;
import com.example.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private InventoryService inventoryService;

    @Autowired
    public InventoryController(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }

    @GetMapping("/restaurants")
    public @ResponseBody List<RestaurantInventoryResponse> inventoryGetAllRestaurants() {
        return inventoryService.getAllRestaurants();
    }

    @GetMapping("/{restaurant_id}/menu")
    public @ResponseBody List<MenuInventoryResponse> inventoryGetRestaurantMenu(@PathVariable("restaurant_id") Long restaurantId) {
        return inventoryService.getRestaurantMenu(restaurantId);
    }

    @GetMapping("/{menu_item_id}")
    public  @ResponseBody MenuInventoryResponse inventoryGetRestaurantMenuItem(@PathVariable("menu_item_id") Long menuItemId) {
        return inventoryService.getMenuItemInventory(menuItemId);
    }

    @PatchMapping("/quantity/{menu_item_id}/{ordered}")
    public ResponseEntity<Void> updateMenuItemQuantity(@PathVariable("menu_item_id") Long menuItemId, @PathVariable("ordered") Integer ordered) {
        inventoryService.updateMenuItemQuantity(menuItemId, ordered);
        return ResponseEntity.ok().build();
    }

}
