package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.Customer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonBackReference
    private Customer customer;

    @Column(name = "points_balance", nullable = false)
    private Integer pointsBalance;

    @Column(name = "lifetime_points", nullable = false)
    private Integer lifetimePoints;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private MembershipTier tier;

    @Column(name = "referral_code", unique = true)
    private String referralCode;

    @Column(name = "referral_count")
    private Integer referralCount;

    @Column(name = "last_tier_update")
    private LocalDateTime lastTierUpdate;

    @Column(name = "auto_renew")
    private Boolean autoRenew;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id")
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "subscription_start_date")
    private LocalDateTime subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private LocalDateTime subscriptionEndDate;

    @Column(name = "subscription_status")
    private String subscriptionStatus;

    @PrePersist
    protected void onCreate() {
        if (pointsBalance == null) {
            pointsBalance = 0;
        }
        if (lifetimePoints == null) {
            lifetimePoints = 0;
        }
        if (tier == null) {
            tier = MembershipTier.BRONZE;
        }
        if (referralCount == null) {
            referralCount = 0;
        }
        if (autoRenew == null) {
            autoRenew = false;
        }
        if (subscriptionStatus == null) {
            subscriptionStatus = "INACTIVE";
        }
    }
}
