package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Inventory;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Sale;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.SaleItem;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.authentication.User;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.SaleRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.SaleRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.authentication.UserService;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.notification.EmailService;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.notification.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class SaleService {
    private static final Logger logger = LoggerFactory.getLogger(SaleService.class);

    @Autowired
    private SaleRepository saleRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SmsService smsService;

    @Transactional
    public Sale createSale(SaleRequest request) {
        logger.debug("Creating new sale for customer: {}", request.getCustomerId());
        
        User customer = userService.getUserById(request.getCustomerId());
        Sale sale = new Sale();
        sale.setCustomer(customer);
        sale.setPaymentMethod(request.getPaymentMethod());
        sale.setDeliveryAddress(request.getDeliveryAddress());
        sale.setDeliveryInstructions(request.getDeliveryInstructions());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleRequest.Item itemRequest : request.getItems()) {
            Inventory inventory = inventoryService.getItemById(itemRequest.getInventoryId())
                    .orElseThrow(() -> new RuntimeException("Inventory item not found"));

            if (inventory.getQuantity() < itemRequest.getQuantity()) {
                throw new RuntimeException("Insufficient inventory for item: " + inventory.getName());
            }

            SaleItem saleItem = new SaleItem();
            saleItem.setSale(sale);
            saleItem.setInventory(inventory);
            saleItem.setQuantity(itemRequest.getQuantity());
            saleItem.setUnitPrice(inventory.getPrice());
            saleItem.setSpecialInstructions(itemRequest.getSpecialInstructions());
            saleItem.calculateTotal();

            sale.addItem(saleItem);
            totalAmount = totalAmount.add(saleItem.getTotalPrice());

            // Update inventory
            inventory.setQuantity(inventory.getQuantity() - itemRequest.getQuantity());
            inventoryService.updateItem(inventory);
        }

        sale.setTotalAmount(totalAmount);
        Sale savedSale = saleRepository.save(sale);

        // Send notifications
        if (customer.isEmailNotifications()) {
            emailService.sendOrderConfirmation(
                customer.getEmail(),
                savedSale.getId().toString(),
                formatOrderDetails(savedSale),
                customer.getPhoneNumber()
            );
        }

        if (customer.isSmsNotifications() && customer.getPhoneNumber() != null) {
            smsService.sendOrderConfirmation(
                customer.getPhoneNumber(),
                formatOrderDetails(savedSale)
            );
        }

        return savedSale;
    }

    @Transactional(readOnly = true)
    public Page<Sale> getAllSales(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Sale getSaleById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
    }

    @Transactional
    public Sale updateSaleStatus(Long id, String status) {
        Sale sale = getSaleById(id);
        sale.setOrderStatus(status);
        
        Sale updatedSale = saleRepository.save(sale);

        // Send status update notification
        User customer = sale.getCustomer();
        if (customer.isEmailNotifications()) {
            emailService.sendOrderStatusUpdate(
                customer.getEmail(),
                customer.getUsername(),
                status,
                "Your order status has been updated",
                formatOrderDetails(updatedSale),
                "30 minutes",
                "http://your-domain.com/track/" + id,
                customer.getPhoneNumber()
            );
        }

        if (customer.isSmsNotifications() && customer.getPhoneNumber() != null) {
            smsService.sendOrderStatusUpdate(
                customer.getPhoneNumber(),
                status,
                "30 minutes"
            );
        }

        return updatedSale;
    }

    @Transactional(readOnly = true)
    public List<Sale> getSalesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return saleRepository.findBySaleDateBetween(startDate, endDate);
    }

    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return saleRepository.existsById(id);
    }

    @Transactional
    public void deleteSaleById(Long id) {
        logger.debug("Deleting sale with ID: {}", id);
        saleRepository.deleteById(id);
    }

    private String formatOrderDetails(Sale sale) {
        StringBuilder details = new StringBuilder();
        details.append("Order #").append(sale.getId()).append("\n");
        details.append("Items:\n");
        for (SaleItem item : sale.getItems()) {
            details.append("- ").append(item.getInventory().getName())
                  .append(" x").append(item.getQuantity())
                  .append(" @ $").append(item.getUnitPrice())
                  .append(" = $").append(item.getTotalPrice()).append("\n");
        }
        details.append("Total: $").append(sale.getTotalAmount());
        return details.toString();
    }
}
