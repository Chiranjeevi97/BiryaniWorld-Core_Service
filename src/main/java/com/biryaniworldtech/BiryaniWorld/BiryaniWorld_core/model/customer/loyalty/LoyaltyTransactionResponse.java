package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.loyalty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoyaltyTransactionResponse {
    private Long id;
    private Integer points;
    private String transactionType;
    private String description;
    private String referenceId;
    private LocalDateTime transactionDate;
} 