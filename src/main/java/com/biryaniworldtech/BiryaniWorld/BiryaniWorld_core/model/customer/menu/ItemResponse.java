package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {

    public Long itemId;

    public String name;

    public String description;

    public double price;

    public String itemQuantity;

    public boolean seasonal;
}
