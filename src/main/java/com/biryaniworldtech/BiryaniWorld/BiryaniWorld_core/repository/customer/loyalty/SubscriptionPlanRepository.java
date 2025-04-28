package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.loyalty;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.SubscriptionPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlan, Long> {
    List<SubscriptionPlan> findByIsActiveTrue();
} 