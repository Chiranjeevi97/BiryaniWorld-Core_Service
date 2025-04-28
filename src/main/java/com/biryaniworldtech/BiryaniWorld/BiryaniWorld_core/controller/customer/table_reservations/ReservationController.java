package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.controller.customer.table_reservations;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.table_reservations.ReservationRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.table_reservations.ReservationResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.table_reservations.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin(origins = "*", maxAge = 3600)
@Tag(name = "Table Reservations", description = "Table reservation management APIs")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Operation(summary = "Create new reservation", description = "Creates a new table reservation for the authenticated customer")
    @ApiResponse(responseCode = "200", description = "Reservation created successfully")
    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(reservationService.createReservation(request, authentication.getName()));
    }

    @Operation(summary = "Get all reservations", description = "Retrieves all reservations for the authenticated customer")
    @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations(Authentication authentication) {
        return ResponseEntity.ok(reservationService.getAllReservations(authentication.getName()));
    }

    @Operation(summary = "Get reservation by ID", description = "Retrieves a specific reservation by ID for the authenticated customer")
    @ApiResponse(responseCode = "200", description = "Reservation retrieved successfully")
    @GetMapping("/{reservationId}")
    public ResponseEntity<ReservationResponse> getReservationById(
            @PathVariable Long reservationId,
            Authentication authentication) {
        return ResponseEntity.ok(reservationService.getReservationById(reservationId, authentication.getName()));
    }

    @Operation(summary = "Request reservation update", description = "Requests an update to an existing reservation")
    @ApiResponse(responseCode = "200", description = "Update request submitted successfully")
    @PutMapping("/{reservationId}/request-update")
    public ResponseEntity<ReservationResponse> requestReservationUpdate(@PathVariable Long reservationId,
            @Valid @RequestBody ReservationRequest request,
            Authentication authentication) {
        return ResponseEntity.ok(reservationService.requestReservationUpdate(request, authentication.getName()));
    }

    @Operation(summary = "Request reservation cancellation", description = "Requests cancellation of a reservation")
    @ApiResponse(responseCode = "200", description = "Cancellation request submitted successfully")
    @PutMapping("/{reservationId}/request-cancellation")
    public ResponseEntity<ReservationResponse> requestReservationCancellation(
            @PathVariable Long reservationId,
            Authentication authentication) {
        return ResponseEntity.ok(reservationService.requestReservationCancellation(reservationId, authentication.getName()));
    }

    @Operation(summary = "Update reservation status", description = "Updates the status of a reservation (Admin only)")
    @ApiResponse(responseCode = "200", description = "Reservation status updated successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/{reservationId}/status")
    public ResponseEntity<ReservationResponse> updateReservationStatus(
            @PathVariable Long reservationId,
            @RequestParam String status,
            Authentication authentication) {
        try {
            return ResponseEntity.ok(reservationService.updateReservationStatus(reservationId, status, authentication.getName()));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ReservationResponse.builder()
                            .status("FORBIDDEN")
                            .specialRequests("You do not have permission to update reservation status. Only administrators can perform this action.")
                            .build());
        }
    }

    @Operation(summary = "Get reservations by date range", description = "Retrieves all reservations within a date range (Admin only)")
    @ApiResponse(responseCode = "200", description = "Reservations retrieved successfully")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/date-range")
    public ResponseEntity<?> getReservationsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        try {
            return ResponseEntity.ok(reservationService.getReservationsByDateRange(fromDate, toDate));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ReservationResponse.builder()
                            .status("FORBIDDEN")
                            .specialRequests("You do not have permission to view all reservations. Only administrators can perform this action.")
                            .build());
        }
    }
}
