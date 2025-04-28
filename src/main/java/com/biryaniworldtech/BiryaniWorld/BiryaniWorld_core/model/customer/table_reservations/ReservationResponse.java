package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.table_reservations;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationResponse {
    private Long id;
    private Integer tableNumber;
    private Integer numberOfGuests;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationDateTime;
    
    private String status;
    private String specialRequests;
    private Long customerId;
    private String customerName;
}
