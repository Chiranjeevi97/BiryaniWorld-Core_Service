package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.order;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.order.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    List<Orders> findByCustomer_CustomerId(Long customerId);
    List<Orders> findByOrderDateTimeBetween(LocalDateTime fromDate, LocalDateTime toDate);
}
