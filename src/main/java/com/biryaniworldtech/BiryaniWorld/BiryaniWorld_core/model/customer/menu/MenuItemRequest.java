package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.menu;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequest {

    @NotNull(message = "Menu ID must not be null")
    @Min(value = 1, message = "Menu ID must be a positive number")
    public Integer menuId;

    @NotNull(message = "Menu name must not be null")
    @NotEmpty(message = "Menu name must not be empty")
    public String name;

    @NotNull(message = "Items list must not be null")
    @NotEmpty(message = "Items list must not be empty")
    public List<ItemResponse> items;

}
