package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemResponse {
    private Long id;
    private Long itemId;
    private String itemName;
    private String description;
    private Integer quantity;
    private Double price;
    private String itemQuantity;
    private Boolean seasonal;
} 