package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.menu;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.menu.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuItemRepository extends JpaRepository<Menu, Integer> {
}
