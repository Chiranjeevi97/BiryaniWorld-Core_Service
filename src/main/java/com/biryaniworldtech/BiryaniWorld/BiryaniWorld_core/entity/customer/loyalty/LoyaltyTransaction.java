package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty;

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
@Table(name = "loyalty_transactions")
public class LoyaltyTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "loyalty_account_id", nullable = false)
    private LoyaltyAccount loyaltyAccount;

    @Column(name = "points", nullable = false)
    private Integer points;

    @Column(name = "transaction_type", nullable = false)
    private String transactionType; // EARNED, REDEEMED, EXPIRED, ADJUSTED

    @Column(name = "description")
    private String description;

    @Column(name = "reference_id")
    private String referenceId; // Order ID or other reference

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @PrePersist
    protected void onCreate() {
        if (transactionDate == null) {
            transactionDate = LocalDateTime.now();
        }
    }
} 