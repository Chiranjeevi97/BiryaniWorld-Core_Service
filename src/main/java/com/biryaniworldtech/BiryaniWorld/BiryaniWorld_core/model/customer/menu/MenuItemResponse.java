package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.menu;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MenuItemResponse {

    public Integer menuId;

    public String name;

    public List<ItemResponse> items;

}
