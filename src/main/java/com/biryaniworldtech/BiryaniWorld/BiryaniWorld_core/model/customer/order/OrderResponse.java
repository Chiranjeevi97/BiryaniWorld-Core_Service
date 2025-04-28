package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.order.OrderItem;
import com.fasterxml.jackson.annotation.JsonFormat;
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
public class OrderResponse {
    private Long orderId;
    private Long customerId;
    private String status;
    private Double totalAmount;
    private List<OrderItemResponse> orderItems;
}