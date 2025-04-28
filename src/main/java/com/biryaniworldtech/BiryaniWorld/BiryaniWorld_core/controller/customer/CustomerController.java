package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.controller.customer;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.Customer;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.InvalidRequestException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.CustomerRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Customer Management", description = "Customer management APIs")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "Get all customers", description = "Retrieves all customers (Admin only)")
    @ApiResponse(responseCode = "200", description = "Customers retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @Operation(summary = "Get customer profile", description = "Retrieves the profile of the authenticated customer")
    @ApiResponse(responseCode = "200", description = "Customer profile retrieved successfully")
    @GetMapping("/me")
    public ResponseEntity<Customer> getCustomerProfile(Authentication authentication) {
        return ResponseEntity.ok(customerService.getCustomerByUserName(authentication.getName()));
    }

    @Operation(summary = "Update customer profile", description = "Updates the profile of the authenticated customer")
    @ApiResponse(responseCode = "200", description = "Customer profile updated successfully")
    @PutMapping("/me")
    public ResponseEntity<Customer> updateCustomerProfile(
            @Valid @RequestBody CustomerRequest customerRequest,
            Authentication authentication) {
        return ResponseEntity.ok(customerService.updateCustomerProfile(customerRequest, authentication.getName()));
    }

    @Operation(summary = "Request account deletion", description = "Requests deletion of the authenticated customer's account")
    @ApiResponse(responseCode = "200", description = "Deletion request submitted successfully")
    @DeleteMapping("/me")
    public ResponseEntity<String> requestAccountDeletion(Authentication authentication) {
        customerService.requestAccountDeletion(authentication.getName());
        return ResponseEntity.ok("Account deletion request submitted successfully");
    }

    @Operation(summary = "Get customer by ID", description = "Retrieves a specific customer by ID (Admin only)")
    @ApiResponse(responseCode = "200", description = "Customer retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{customerId}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.getCustomerById(customerId)
                .orElseThrow(() -> new InvalidRequestException("Customer not found with ID: " + customerId)));
    }

    @Operation(summary = "Create customer", description = "Creates a new customer (Admin only)")
    @ApiResponse(responseCode = "200", description = "Customer created successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<Customer> createCustomer(@Valid @RequestBody CustomerRequest customerRequest) {
        return ResponseEntity.ok(customerService.createCustomer(customerRequest));
    }

    @Operation(summary = "Update customer", description = "Updates an existing customer (Admin only)")
    @ApiResponse(responseCode = "200", description = "Customer updated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{customerId}")
    public ResponseEntity<Customer> updateCustomer(
            @Valid @RequestBody CustomerRequest customerRequest,
            @PathVariable Long customerId) {
        return ResponseEntity.ok(customerService.updateCustomer(customerRequest, customerId));
    }

    @Operation(summary = "Delete customer", description = "Deletes a customer (Admin only)")
    @ApiResponse(responseCode = "200", description = "Customer deleted successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{customerId}")
    public ResponseEntity<String> deleteCustomer(@PathVariable Long customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok("Customer deleted successfully");
    }
}
