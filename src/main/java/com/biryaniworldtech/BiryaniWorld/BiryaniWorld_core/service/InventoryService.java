package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Inventory;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.InventoryRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.InventoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {
    private static final Logger logger = LoggerFactory.getLogger(InventoryService.class);

    @Autowired
    private InventoryRepository inventoryRepository;

    @Value("${inventory.reorder.level:10}")
    private Integer reorderLevel;

    @Transactional(readOnly = true)
    public Page<Inventory> getAllInventory(Pageable pageable) {
        return inventoryRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Optional<Inventory> getItemById(Long id) {
        return inventoryRepository.findById(id);
    }

    @Transactional
    public Inventory createItem(InventoryRequest request) {
        logger.debug("Creating new inventory item: {}", request.getName());
        Inventory item = new Inventory();
        updateItemFromRequest(item, request);
        return inventoryRepository.save(item);
    }

    @Transactional
    public Inventory updateItem(Inventory item) {
        logger.debug("Updating inventory item: {}", item.getName());
        return inventoryRepository.save(item);
    }

    @Transactional
    public Inventory updateItemById(InventoryRequest request, Long id) {
        logger.debug("Updating inventory item with ID: {}", id);
        Inventory item = getItemById(id)
                .orElseThrow(() -> new RuntimeException("Inventory item not found"));
        updateItemFromRequest(item, request);
        return inventoryRepository.save(item);
    }

    @Transactional
    public void deleteItemById(Long id) {
        logger.debug("Deleting inventory item with ID: {}", id);
        inventoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Inventory> getLowStockItems() {
        return inventoryRepository.findByQuantityLessThanEqual(reorderLevel);
    }

    @Transactional(readOnly = true)
    public List<Inventory> getItemsByCategory(String category) {
        return inventoryRepository.findByCategory(category);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return inventoryRepository.existsById(id);
    }

    private void updateItemFromRequest(Inventory item, InventoryRequest request) {
        item.setName(request.getName());
        item.setCategory(request.getCategory());
        item.setQuantity(request.getQuantity());
        item.setPrice(request.getPrice());
        item.setDescription(request.getDescription());
        item.setReorderLevel(request.getReorderLevel());
        item.setSupplierInfo(request.getSupplierInfo());
    }
}
