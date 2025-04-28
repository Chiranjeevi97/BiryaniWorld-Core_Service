package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.controller;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Inventory;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.InvalidRequestException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.InventoryRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {

    @Autowired
    InventoryService inventoryService;

    @Operation(summary = "Get all inventory items", description = "Retrieves a list of all inventory items")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved all inventory items"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping
    public Page<Inventory> getALlInventory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return inventoryService.getAllInventory(PageRequest.of(page, size));
    }

    @Operation(summary = "Get inventory item by ID", description = "Retrieves a specific inventory item by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved the inventory item"),
        @ApiResponse(responseCode = "404", description = "Inventory item not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/{inventory-id}")
    public Optional<Inventory> getInventory(@PathVariable("inventory-id") Long inventoryId) {
        if(inventoryService.existsById(inventoryId))
            return inventoryService.getItemById(inventoryId);
        else
            throw new InvalidRequestException("Inventory Item with ID " + inventoryId + " does not exist.");
    }

    @Operation(summary = "Create new inventory item", description = "Creates a new inventory item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully created inventory item"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PostMapping
    public String createInventory(@Valid @RequestBody InventoryRequest inventoryRequest) {
        inventoryService.createItem(inventoryRequest);
        return "Created Inventory";
    }

    @Operation(summary = "Update inventory item", description = "Updates an existing inventory item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully updated inventory item"),
        @ApiResponse(responseCode = "404", description = "Inventory item not found"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PutMapping("/update/{inventory-id}")
    public Inventory updateInventory(@Valid @RequestBody InventoryRequest inventoryRequest, @PathVariable("inventory-id") Long inventoryId) {
        if(inventoryService.existsById(inventoryId))
            return inventoryService.updateItemById(inventoryRequest, inventoryId);
        else
            throw new InvalidRequestException("Inventory Item with ID " + inventoryId + " does not exist.");
    }

    @Operation(summary = "Delete inventory item", description = "Deletes an inventory item")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted inventory item"),
        @ApiResponse(responseCode = "404", description = "Inventory item not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @DeleteMapping("/delete/{inventory-id}")
    public String deleteInventory(@PathVariable("inventory-id") Long inventoryId) {
        if(inventoryService.existsById(inventoryId))
            inventoryService.deleteItemById(inventoryId);
        else
            throw new InvalidRequestException("Inventory Item with ID " + inventoryId + " does not exist.");
        return "Deleted Inventory - " + inventoryId;
    }
}
