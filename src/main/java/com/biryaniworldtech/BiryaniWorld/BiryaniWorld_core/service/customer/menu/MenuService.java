package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.menu;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.menu.Item;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.menu.Menu;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.InvalidRequestException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.menu.MenuItemRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.menu.MenuItemResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.menu.ItemResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.menu.ItemRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.menu.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuService {

    @Autowired
    MenuItemRepository menuItemRepository;

    @Autowired
    ItemRepository itemRepository;

    public List<MenuItemResponse> getMenu() {
        List<Menu> menus = menuItemRepository.findAll();
        return menus.stream()
                .map(menu -> MenuItemResponse.builder()
                        .menuId(menu.getMenuId())
                        .name(menu.getName())
                        .items(menu.getItems().stream()
                                .map(item -> ItemResponse.builder()
                                        .itemId(item.getItemId())
                                        .name(item.getName())
                                        .description(item.getDescription())
                                        .price(item.getPrice())
                                        .itemQuantity(item.getItemQuantity())
                                        .seasonal(item.isSeasonal())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    public MenuItemResponse getMenuWithId(String menuId) {
        Optional<Menu> menu = menuItemRepository.findById(Integer.valueOf(menuId));
        if(menu.isPresent()) {
            Menu menuEntity = menu.get();
            return MenuItemResponse.builder()
                    .menuId(menuEntity.getMenuId())
                    .name(menuEntity.getName())
                    .items(menuEntity.getItems().stream()
                            .map(item -> ItemResponse.builder()
                                    .itemId(item.getItemId())
                                    .name(item.getName())
                                    .description(item.getDescription())
                                    .price(item.getPrice())
                                    .itemQuantity(item.getItemQuantity())
                                    .seasonal(item.isSeasonal())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        }
        throw new InvalidRequestException("Menu with ID " + menuId + " does not exist.");
    }

    public MenuItemResponse createMenu(MenuItemRequest menuItemRequest) {
        Menu menu = new Menu();
        menu.setMenuId(menuItemRequest.getMenuId());
        menu.setName(menuItemRequest.getName());
        menu.setItems(menuItemRequest.getItems().stream()
                .map(itemRequest -> {
                    Item item = new Item();
                    item.setItemId(itemRequest.getItemId());
                    item.setName(itemRequest.getName());
                    item.setDescription(itemRequest.getDescription());
                    item.setPrice(itemRequest.getPrice());
                    item.setItemQuantity(itemRequest.getItemQuantity());
                    item.setSeasonal(itemRequest.isSeasonal());
                    item.setMenu(menu);
                    return item;
                })
                .collect(Collectors.toList()));
        
        List<Menu> menuList = menuItemRepository.findAll();
        if(menuList.stream().anyMatch(menu1 -> menu1.getMenuId().equals(menu.getMenuId()))) {
            throw new InvalidRequestException("Menu with ID " + menu.getMenuId() + " already exist.");
        }
        menuItemRepository.save(menu);
        
        return MenuItemResponse.builder()
                .menuId(menu.getMenuId())
                .name(menu.getName())
                .items(menu.getItems().stream()
                        .map(item -> ItemResponse.builder()
                                .itemId(item.getItemId())
                                .name(item.getName())
                                .description(item.getDescription())
                                .price(item.getPrice())
                                .itemQuantity(item.getItemQuantity())
                                .seasonal(item.isSeasonal())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    public MenuItemResponse updateMenuCategory(MenuItemRequest menuItemRequest, String menuId) {
        if(!menuItemRequest.getMenuId().equals(Integer.valueOf(menuId)))
            throw new InvalidRequestException("Menu Id is not matching with the Request Menu Id Please check it!");
        Optional<Menu> foundMenu = menuItemRepository.findById(Integer.valueOf(menuId));
        if(foundMenu.isPresent()) {
            Menu menu = foundMenu.get();
            menu.setName(menuItemRequest.getName());
            menu.setItems(menuItemRequest.getItems().stream()
                    .map(itemRequest -> {
                        Item item = new Item();
                        item.setItemId(itemRequest.getItemId());
                        item.setName(itemRequest.getName());
                        item.setDescription(itemRequest.getDescription());
                        item.setPrice(itemRequest.getPrice());
                        item.setItemQuantity(itemRequest.getItemQuantity());
                        item.setSeasonal(itemRequest.isSeasonal());
                        item.setMenu(menu);
                        return item;
                    })
                    .collect(Collectors.toList()));
            menuItemRepository.save(menu);
            
            return MenuItemResponse.builder()
                    .menuId(menu.getMenuId())
                    .name(menu.getName())
                    .items(menu.getItems().stream()
                            .map(item -> ItemResponse.builder()
                                    .itemId(item.getItemId())
                                    .name(item.getName())
                                    .description(item.getDescription())
                                    .price(item.getPrice())
                                    .itemQuantity(item.getItemQuantity())
                                    .seasonal(item.isSeasonal())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        }
        throw new InvalidRequestException("Menu with ID " + menuId + " does not exist.");
    }

    public MenuItemResponse updateMenuItem(MenuItemRequest menuItemRequest, String menuId) {
        Optional<Menu> foundMenu = menuItemRepository.findById(Integer.valueOf(menuId));
        if(foundMenu.isPresent()) {
            Menu menu = foundMenu.get();
            menu.setName(menuItemRequest.getName());
            menu.setItems(menuItemRequest.getItems().stream()
                    .map(itemRequest -> {
                        Item item = new Item();
                        item.setItemId(itemRequest.getItemId());
                        item.setName(itemRequest.getName());
                        item.setDescription(itemRequest.getDescription());
                        item.setPrice(itemRequest.getPrice());
                        item.setItemQuantity(itemRequest.getItemQuantity());
                        item.setSeasonal(itemRequest.isSeasonal());
                        item.setMenu(menu);
                        return item;
                    })
                    .collect(Collectors.toList()));
            menuItemRepository.save(menu);
            
            return MenuItemResponse.builder()
                    .menuId(menu.getMenuId())
                    .name(menu.getName())
                    .items(menu.getItems().stream()
                            .map(item -> ItemResponse.builder()
                                    .itemId(item.getItemId())
                                    .name(item.getName())
                                    .description(item.getDescription())
                                    .price(item.getPrice())
                                    .itemQuantity(item.getItemQuantity())
                                    .seasonal(item.isSeasonal())
                                    .build())
                            .collect(Collectors.toList()))
                    .build();
        }
        throw new InvalidRequestException("Menu with ID " + menuId + " does not exist.");
    }

    public void deleteMenu(String menuId) {
        Optional<Menu> menu = menuItemRepository.findById(Integer.valueOf(menuId));
        if(menu.isEmpty())
            throw new InvalidRequestException("Menu with ID " + menuId + " does not exist.");
        else
            menuItemRepository.deleteById(Integer.valueOf(menuId));
    }

    public void deleteMenuItem(String menuId, String itemId) {
        Optional<Menu> menuOptional = menuItemRepository.findById(Integer.valueOf(menuId));
        Optional<Item> itemOptional = itemRepository.findById(Long.valueOf(itemId));
        if(menuOptional.isEmpty() || itemOptional.isEmpty())
            throw new InvalidRequestException("Menu with ID " + menuId + " or Item with Id " + itemId + " does not exist.");
        else {
            Menu menu = menuOptional.get();
            Item item = itemOptional.get();
            menu.getItems().remove(item);
            item.setMenu(null);
            menuItemRepository.save(menu);
        }
    }
}
