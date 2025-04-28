package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.controller.customer.order;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order.OrderRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order.OrderResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.order.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Order Management", description = "Order management APIs")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Operation(summary = "Create new order", description = "Creates a new order for the authenticated customer")
    @ApiResponse(responseCode = "200", description = "Order created successfully")
    @PostMapping
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderRequest orderRequest,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.createOrder(orderRequest, authentication.getName()));
    }

    @Operation(summary = "Get all orders", description = "Retrieves all orders for the authenticated customer")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(Authentication authentication) {
        return ResponseEntity.ok(orderService.getAllOrders(authentication.getName()));
    }

    @Operation(summary = "Get order by ID", description = "Retrieves a specific order by ID for the authenticated customer")
    @ApiResponse(responseCode = "200", description = "Order retrieved successfully")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long orderId,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.getOrderById(orderId, authentication.getName()));
    }

    @Operation(summary = "Request order cancellation", description = "Requests cancellation of an order")
    @ApiResponse(responseCode = "200", description = "Cancellation request submitted successfully")
    @PutMapping("/{orderId}/request-cancellation")
    public ResponseEntity<?> requestOrderCancellation(
            @PathVariable Long orderId,
            Authentication authentication) {
        return ResponseEntity.ok(orderService.requestOrderCancellation(orderId, authentication.getName()));
    }

    @Operation(summary = "Update order status", description = "Updates the status of an order (Admin only)")
    @ApiResponse(responseCode = "200", description = "Order status updated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @Operation(summary = "Get orders by date range", description = "Retrieves all orders within a date range (Admin only)")
    @ApiResponse(responseCode = "200", description = "Orders retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/date-range")
    public ResponseEntity<List<OrderResponse>> getOrdersByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        return ResponseEntity.ok(orderService.getOrdersByDateRange(fromDate, toDate));
    }
}
