package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.controller.customer.loyalty;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.loyalty.LoyaltyDashboardResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.loyalty.SubscriptionResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.loyalty.LoyaltyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loyalty")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Loyalty Program", description = "Loyalty program and subscription management APIs")
public class LoyaltyController {

    @Autowired
    private LoyaltyService loyaltyService;

    @Operation(summary = "Get loyalty dashboard", description = "Retrieves the loyalty dashboard for the authenticated customer")
    @ApiResponse(responseCode = "200", description = "Loyalty dashboard retrieved successfully")
    @GetMapping("/dashboard")
    public ResponseEntity<LoyaltyDashboardResponse> getLoyaltyDashboard(Authentication authentication) {
        return ResponseEntity.ok(loyaltyService.getLoyaltyDashboard(authentication.getName()));
    }

    @Operation(summary = "Subscribe to plan", description = "Subscribes the authenticated customer to a subscription plan")
    @ApiResponse(responseCode = "200", description = "Subscription created successfully")
    @PostMapping("/subscribe/{planId}")
    public ResponseEntity<SubscriptionResponse> subscribeToPlan(
            @PathVariable Long planId,
            Authentication authentication) {
        return ResponseEntity.ok(loyaltyService.subscribeToPlan(planId, authentication.getName()));
    }

    @Operation(summary = "Toggle auto-renew", description = "Toggles the auto-renew setting for the authenticated customer's subscription")
    @ApiResponse(responseCode = "200", description = "Auto-renew setting updated successfully")
    @PutMapping("/auto-renew")
    public ResponseEntity<Void> toggleAutoRenew(Authentication authentication) {
        loyaltyService.toggleAutoRenew(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cancel subscription", description = "Cancels the authenticated customer's subscription")
    @ApiResponse(responseCode = "200", description = "Subscription cancelled successfully")
    @DeleteMapping("/subscription")
    public ResponseEntity<Void> cancelSubscription(Authentication authentication) {
        loyaltyService.cancelSubscription(authentication.getName());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Process referral", description = "Processes a referral for a new customer")
    @ApiResponse(responseCode = "200", description = "Referral processed successfully")
    @PostMapping("/referral")
    public ResponseEntity<Void> processReferral(
            @RequestParam String referralCode,
            @RequestParam Long newCustomerId) {
        loyaltyService.processReferral(referralCode, newCustomerId);
        return ResponseEntity.ok().build();
    }
}
