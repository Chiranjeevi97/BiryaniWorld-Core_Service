package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.loyalty;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.MembershipTier;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.SubscriptionPlan;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoyaltyDashboardResponse {
    private Integer pointsBalance;
    private Integer lifetimePoints;
    private MembershipTier tier;
    private String referralCode;
    private Integer referralCount;
    private SubscriptionPlan subscriptionPlan;
    private String subscriptionStatus;
    private LocalDateTime subscriptionEndDate;
    private Boolean autoRenew;
    private List<LoyaltyTransactionResponse> recentTransactions;
} 