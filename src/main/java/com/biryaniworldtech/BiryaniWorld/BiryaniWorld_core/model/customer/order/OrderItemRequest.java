package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItemRequest {

    private Long itemId;

    private Integer quantity;

    private Double price;

}
