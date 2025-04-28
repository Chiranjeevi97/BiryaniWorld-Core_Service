package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.controller;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Sale;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.InvalidRequestException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.SaleRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.SaleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sales")
public class SalesController {


    @Autowired
    SaleService saleService;

    @GetMapping
    public Page<Sale> getAllSalesReport(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return saleService.getAllSales(PageRequest.of(page, size));
    }

    @GetMapping("/{sale-id}")
    public Sale getSale(@PathVariable("sale-id") Long saleId) {
        if(saleService.existsById(saleId))
            return saleService.getSaleById(saleId);
        else
            throw new InvalidRequestException("Sale with ID " + saleId + " does not exist.");
    }

    @PostMapping
    public Sale createSalesReport(@Valid @RequestBody SaleRequest saleRequest){
        return saleService.createSale(saleRequest);
    }

    @PutMapping("/update/{sale-id}")
    public String updateSalesReport(@PathVariable("sale-id") Long saleId, @Valid @RequestBody SaleRequest saleRequest) {
        if(saleService.existsById(saleId))
            saleService.updateSaleStatus(saleId, "UPDATED");
        else
            throw new InvalidRequestException("Sale with ID " + saleId + " does not exist.");
        return "Updated Sale for - " + saleId;
    }

    @DeleteMapping("/delete/{sale-id}")
    public String deleteSalesReport(@PathVariable("sale-id") Long saleId) {
        if(saleService.existsById(saleId))
            saleService.deleteSaleById(saleId);
        else
            throw new InvalidRequestException("Sale with ID " + saleId + " does not exist.");
        return "Deleted Sale for - " + saleId;
    }
}
