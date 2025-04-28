package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.menu;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.menu.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
}
