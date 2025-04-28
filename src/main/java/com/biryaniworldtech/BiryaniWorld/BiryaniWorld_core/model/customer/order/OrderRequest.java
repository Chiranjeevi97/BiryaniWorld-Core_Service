package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.order;

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
public class OrderRequest {

    public Long orderId;

    public String customerName;

    public Boolean orderFullFilled;

    public String orderStatus;

    public Double totalAmount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    public LocalDateTime orderDateTime;

    public List<OrderItemRequest> items;
}

