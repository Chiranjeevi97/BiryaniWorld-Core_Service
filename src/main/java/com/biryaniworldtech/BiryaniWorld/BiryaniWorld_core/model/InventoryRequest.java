package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryRequest {

    @JsonProperty("item_id")
    @NotNull(message = "Item ID is required")
    @Positive(message = "Item ID must be positive")
    private Integer itemId;

    @JsonProperty("name")
    @NotBlank(message = "Item name is required")
    @Size(max = 100, message = "Item name cannot exceed 100 characters")
    private String name;

    @JsonProperty("category")
    @NotBlank(message = "Category is required")
    private String category;

    @JsonProperty("description")
    @Size(max = 500, message = "Description can't exceed 500 characters")
    private String description;

    @JsonProperty("brand")
    @Size(max = 100, message = "Brand name can't exceed 100 characters")
    private String brand;

    @JsonProperty("quantity")
    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @JsonProperty("unit_of_measure")
    @NotBlank(message = "Unit of measure is required")
    private String unitOfMeasure;

    @JsonProperty("reorder_level")
    @NotNull(message = "Reorder level is required")
    @Min(value = 0, message = "Reorder level cannot be negative")
    private Integer reorderLevel;

    @JsonProperty("reorder_quantity")
    @NotNull(message = "Reorder quantity is required")
    @Min(value = 0, message = "Reorder quantity cannot be negative")
    private Integer reorderQuantity;

    @JsonProperty("par_level")
    @NotNull(message = "Par level is required")
    @Min(value = 0, message = "Par level cannot be negative")
    private Integer parLevel;

    @JsonProperty("unit_cost")
    @NotNull(message = "Unit cost is required")
    @PositiveOrZero(message = "Unit cost cannot be negative")
    private Float unitCost;

    @JsonProperty("total_cost")
    @NotNull(message = "Total cost is required")
    @PositiveOrZero(message = "Total cost cannot be negative")
    private Float totalCost;

    @JsonProperty("purchase_date")
    @NotNull(message = "Purchase date is required")
    @PastOrPresent(message = "Purchase date cannot be in the future")
    private Date purchaseDate;

    @JsonProperty("expiry_date")
    @Future(message = "Expiry date must be in the future")
    private Date expiryDate;

    @JsonProperty("last_updated")
    @PastOrPresent(message = "Last updated date cannot be in the future")
    private Date lastUpdated;

    @JsonProperty("sku")
    @NotBlank(message = "SKU is required")
    private String sku;

    @JsonProperty("image_url")
    @Size(max = 255, message = "Image URL is too long")
    private String imageUrl;

    @NotNull
    @Min(0)
    private BigDecimal price;

    private String supplierInfo;
}
