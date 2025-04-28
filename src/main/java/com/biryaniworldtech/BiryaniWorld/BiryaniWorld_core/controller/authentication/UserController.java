package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.controller.authentication;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.authentication.UserUpdateRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.authentication.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "User Management", description = "User management APIs")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Get user by username", description = "Retrieves user details by username")
    @ApiResponse(responseCode = "200", description = "User details retrieved successfully")
    @GetMapping("/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username));
    }

    @Operation(summary = "Delete user", description = "Deletes a user by username")
    @ApiResponse(responseCode = "200", description = "User deleted successfully")
    @DeleteMapping("/{username}")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        userService.deleteUserByUsername(username);
        return ResponseEntity.ok("User deleted successfully");
    }

    @Operation(summary = "Update user details", description = "Updates the current user's details")
    @ApiResponse(responseCode = "200", description = "User updated successfully")
    @PutMapping("/me")
    public ResponseEntity<?> updateUser(
            @Valid @RequestBody UserUpdateRequest updateRequest,
            Authentication authentication) {
        return ResponseEntity.ok(userService.updateUser(
            userService.getUserByUsername(authentication.getName()).getId(),
            updateRequest
        ));
    }
} 