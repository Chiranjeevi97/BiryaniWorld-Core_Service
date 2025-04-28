package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {
    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT s FROM Sale s WHERE s.customer.id = ?1")
    Page<Sale> findByCustomerId(Long customerId, Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE s.orderStatus = ?1")
    Page<Sale> findByOrderStatus(String status, Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE s.paymentStatus = ?1")
    Page<Sale> findByPaymentStatus(String status, Pageable pageable);

    @Query("SELECT SUM(s.totalAmount) FROM Sale s WHERE s.saleDate BETWEEN ?1 AND ?2")
    BigDecimal getTotalSalesAmount(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.saleDate BETWEEN ?1 AND ?2")
    Long getTotalSalesCount(LocalDateTime startDate, LocalDateTime endDate);
}
