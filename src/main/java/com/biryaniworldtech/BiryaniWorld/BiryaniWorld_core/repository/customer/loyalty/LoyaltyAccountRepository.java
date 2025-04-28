package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.loyalty;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.LoyaltyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoyaltyAccountRepository extends JpaRepository<LoyaltyAccount, Long> {
    Optional<LoyaltyAccount> findByCustomer_CustomerId(Long customerId);
    List<LoyaltyAccount> findBySubscriptionEndDateBeforeAndAutoRenewTrue(LocalDateTime date);
    List<LoyaltyAccount> findBySubscriptionEndDateBetween(LocalDateTime start, LocalDateTime end);
    Optional<LoyaltyAccount> findByReferralCode(String referralCode);
} 