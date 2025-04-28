package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.controller.customer.menu;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.menu.MenuItemRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.menu.MenuItemResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.menu.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menu")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Menu Management", description = "Menu management APIs")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Operation(summary = "Get menu", description = "Retrieves the menu for all users")
    @ApiResponse(responseCode = "200", description = "Menu retrieved successfully")
    @GetMapping("/{location}")
    public ResponseEntity<List<MenuItemResponse>> getMenu() {
        return ResponseEntity.ok(menuService.getMenu());
    }

    @Operation(summary = "Get menu item by ID", description = "Retrieves a specific menu item by ID")
    @ApiResponse(responseCode = "200", description = "Menu item retrieved successfully")
    @GetMapping("/{menu-id}/{location}")
    public ResponseEntity<MenuItemResponse> getMenuWithId(@PathVariable("menu-id") String menuId) {
        return ResponseEntity.ok(menuService.getMenuWithId(menuId));
    }

    @Operation(summary = "Create menu item", description = "Creates a new menu item (Admin only)")
    @ApiResponse(responseCode = "200", description = "Menu item created successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<MenuItemResponse> createMenu(@Valid @RequestBody MenuItemRequest menuItemRequest) {
        return ResponseEntity.ok(menuService.createMenu(menuItemRequest));
    }

    @Operation(summary = "Update menu item", description = "Updates an existing menu item (Admin only)")
    @ApiResponse(responseCode = "200", description = "Menu item updated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{menu-id}")
    public ResponseEntity<MenuItemResponse> updateMenu(
            @Valid @RequestBody MenuItemRequest menuItemRequest,
            @PathVariable("menu-id") String menuId) {
        return ResponseEntity.ok(menuService.updateMenuCategory(menuItemRequest, menuId));
    }

    @Operation(summary = "Delete menu category", description = "Deletes a menu category (Admin only)")
    @ApiResponse(responseCode = "200", description = "Menu category deleted successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{menu-id}")
    public ResponseEntity<String> deleteMenuCategory(@PathVariable("menu-id") String menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.ok("Successfully Deleted!");
    }

    @Operation(summary = "Delete menu item", description = "Deletes a specific menu item (Admin only)")
    @ApiResponse(responseCode = "200", description = "Menu item deleted successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{menu-id}/item/{item-id}")
    public ResponseEntity<String> deleteMenuItemWithItemId(
            @PathVariable("menu-id") String menuId,
            @PathVariable("item-id") String itemId) {
        menuService.deleteMenuItem(menuId, itemId);
        return ResponseEntity.ok("Successfully Deleted!");
    }
}
