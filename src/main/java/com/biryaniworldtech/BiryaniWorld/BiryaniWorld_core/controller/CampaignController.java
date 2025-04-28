package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.controller;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Campaign;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.CampaignRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.CampaignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/campaigns")
@Tag(name = "Campaign Management", description = "Campaign management APIs")
@PreAuthorize("hasRole('ADMIN')")
public class CampaignController {

    @Autowired
    private CampaignService campaignService;

    @Operation(summary = "Create new campaign", description = "Creates a new email campaign")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaign created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PostMapping
    public ResponseEntity<Campaign> createCampaign(@Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.ok(campaignService.createCampaign(request));
    }

    @Operation(summary = "Get all campaigns", description = "Retrieves all email campaigns")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaigns retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping
    public ResponseEntity<List<Campaign>> getAllCampaigns() {
        return ResponseEntity.ok(campaignService.getAllCampaigns());
    }

    @Operation(summary = "Get campaign by ID", description = "Retrieves a specific campaign by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaign retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Campaign not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Campaign> getCampaign(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }

    @Operation(summary = "Update campaign", description = "Updates an existing campaign")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaign updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Campaign not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Campaign> updateCampaign(@PathVariable Long id, @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.ok(campaignService.updateCampaign(id, request));
    }

    @Operation(summary = "Delete campaign", description = "Deletes a campaign")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Campaign deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Campaign not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCampaign(@PathVariable Long id) {
        campaignService.deleteCampaign(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get campaign analytics", description = "Retrieves analytics for a specific campaign")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Campaign not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    @GetMapping("/{id}/analytics")
    public ResponseEntity<Campaign> getCampaignAnalytics(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getCampaignById(id));
    }
} 