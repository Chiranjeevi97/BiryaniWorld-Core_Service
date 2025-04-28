package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleRequest {

    @JsonProperty("id")
    @NotNull(message = "Sale ID is required")
    @Positive(message = "Sale ID must be a positive number")
    private Long id;

    @JsonProperty("saleDate")
    @PastOrPresent(message = "Sale date must be in the past or present")
    private Date saleDate;

    @JsonProperty("totalRevenue")
    @NotNull(message = "Total revenue is required")
    @PositiveOrZero(message = "Total revenue cannot be negative")
    private Float totalRevenue;

    @JsonProperty("totalOrders")
    @NotNull(message = "Total orders is required")
    @Min(value = 0, message = "Total orders cannot be negative")
    private Integer totalOrders;

    @JsonProperty("totalItemsSold")
    @NotNull(message = "Total items sold is required")
    @Min(value = 0, message = "Total items sold cannot be negative")
    private Integer totalItemsSold;

    @JsonProperty("cashAmount")
    @PositiveOrZero(message = "Cash amount cannot be negative")
    private Float cashAmount;

    @JsonProperty("cardAmount")
    @PositiveOrZero(message = "Card amount cannot be negative")
    private Float cardAmount;

    @JsonProperty("onlinePaymentAmount")
    @PositiveOrZero(message = "Online payment amount cannot be negative")
    private Float onlinePaymentAmount;

    @JsonProperty("isNotified")
    @NotNull(message = "Notification status is required")
    private Boolean isNotified;

    @JsonProperty("createdAt")
    @PastOrPresent(message = "Created date must be in the past or present")
    private Date createdAt;

    @JsonProperty("updatedAt")
    @PastOrPresent(message = "Updated date must be in the past or present")
    private Date updatedAt;

    @JsonProperty("notes")
    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @NotNull
    private Long customerId;

    @NotBlank
    private String paymentMethod;

    private String deliveryAddress;

    private String deliveryInstructions;

    @NotEmpty
    @Valid
    private List<Item> items;

    @Data
    public static class Item {
        @NotNull
        private Long inventoryId;

        @NotNull
        private Integer quantity;

        private String specialInstructions;
    }
}
