package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.table_reservations;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequest {

    private Long reservationId;

    @NotNull(message = "Table number is required")
    @Min(value = 1, message = "Table number must be positive")
    private Integer tableNumber;

    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be positive")
    private Integer numberOfGuests;

    @NotNull(message = "Reservation date and time is required")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime reservationDateTime;

    private String specialRequests;

    private String requestType;
}