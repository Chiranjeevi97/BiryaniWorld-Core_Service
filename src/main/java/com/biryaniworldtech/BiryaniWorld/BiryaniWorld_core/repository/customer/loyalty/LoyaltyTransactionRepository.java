package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.loyalty;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.LoyaltyTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoyaltyTransactionRepository extends JpaRepository<LoyaltyTransaction, Long> {
    List<LoyaltyTransaction> findByLoyaltyAccountIdOrderByTransactionDateDesc(Long loyaltyAccountId);
    List<LoyaltyTransaction> findByLoyaltyAccountIdAndTransactionDateBetween(Long loyaltyAccountId, LocalDateTime start, LocalDateTime end);
} 