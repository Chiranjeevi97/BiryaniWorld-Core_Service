package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByQuantityLessThanEqual(Integer reorderLevel);
    
    List<Inventory> findByCategory(String category);
    
    @Query("SELECT i FROM Inventory i WHERE i.name LIKE %?1% OR i.description LIKE %?1%")
    Page<Inventory> searchInventory(String searchTerm, Pageable pageable);
    
    @Query("SELECT i FROM Inventory i WHERE i.quantity <= i.reorderLevel")
    List<Inventory> findLowStockItems();
    
    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.quantity <= i.reorderLevel")
    Long countLowStockItems();
    
    @Query("SELECT SUM(i.quantity * i.price) FROM Inventory i")
    Double getTotalInventoryValue();
}
