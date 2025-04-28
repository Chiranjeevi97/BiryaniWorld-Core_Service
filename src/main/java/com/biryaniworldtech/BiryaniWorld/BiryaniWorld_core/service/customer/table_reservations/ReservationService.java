package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.table_reservations;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.authentication.User;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.Customer;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.table_reservations.Reservation;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.InvalidRequestException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.NoDataFoundException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.table_reservations.ReservationRequest;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.table_reservations.ReservationResponse;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.CustomerRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.table_reservations.ReservationRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.authentication.UserService;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.CustomerService;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.notification.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

    @Autowired
    private CustomerService customerService;

    @Transactional
    public ReservationResponse createReservation(ReservationRequest request, String username) {
        logger.debug("Creating reservation for user: {}", username);
        
        // Get user and verify customer exists
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        // Check if table is available
        if (isTableAvailable(request.getTableNumber(), request.getReservationDateTime())) {
            throw new InvalidRequestException("Table " + request.getTableNumber() + " is not available at the requested time");
        }

        // Create and save the reservation
        Reservation reservation = new Reservation();
        reservation.setTableNumber(request.getTableNumber());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setReservationDateTime(request.getReservationDateTime());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setCustomer(customer);
        reservation.setStatus("PENDING");

        Reservation savedReservation = reservationRepository.save(reservation);
        logger.info("Reservation created successfully with ID: {}", savedReservation.getId());
        emailService.sendReservationConfirmation(customer.getEmail(), customer.getFirstName(), reservation.getId().toString(), reservation.getReservationDateTime().toString(), reservation.getStatus(), reservation.getNumberOfGuests());
        return mapToReservationResponse(savedReservation);
    }

    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations(String username) {
        logger.debug("Fetching all reservations for user: {}", username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        return reservationRepository.findByCustomer_CustomerId(customer.getCustomerId()).stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long reservationId, String username) {
        logger.debug("Fetching reservation {} for user: {}", reservationId, username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoDataFoundException("Reservation not found with ID: " + reservationId));

        if (!reservation.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new NoDataFoundException("Reservation not found for user: " + username);
        }

        return mapToReservationResponse(reservation);
    }

    @Transactional
    public ReservationResponse updateReservation(ReservationRequest request, Long reservationId, String username) {
        logger.debug("Updating reservation {} for user: {}", reservationId, username);

        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();

        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoDataFoundException("Reservation not found with ID: " + reservationId));

        if (!reservation.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new NoDataFoundException("Reservation not found for user: " + username);
        }
        Reservation updatedReservation = reservationRepository.save(reservation);
        logger.info("Reservation status updated successfully for reservation ID: {}", reservationId);

        if (isTableAvailable(request.getTableNumber(), request.getReservationDateTime())) {
            throw new InvalidRequestException("Table " + request.getTableNumber() + " is not available at the requested time");
        }
        reservation.setTableNumber(request.getTableNumber());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setReservationDateTime(request.getReservationDateTime());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setCustomer(customer);
        reservation.setStatus("PENDING");
        return mapToReservationResponse(updatedReservation);
    }

    @Transactional
    public ReservationResponse updateReservationStatus(Long reservationId, String status, String username) {
        logger.debug("Updating reservation {} status to {} for user: {}", reservationId, status, username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoDataFoundException("Reservation not found with ID: " + reservationId));

        if (!reservation.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new NoDataFoundException("Reservation not found for user: " + username);
        }

        reservation.setStatus(status);
        Reservation updatedReservation = reservationRepository.save(reservation);
        logger.info("Reservation status updated successfully for reservation ID: {}", reservationId);
        
        return mapToReservationResponse(updatedReservation);
    }

    @Transactional
    public void cancelReservation(Long reservationId, String username) {
        logger.debug("Cancelling reservation {} for user: {}", reservationId, username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoDataFoundException("Reservation not found with ID: " + reservationId));

        if (!reservation.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new NoDataFoundException("Reservation not found for user: " + username);
        }

        reservation.setStatus("CANCELLED");
        reservationRepository.save(reservation);
        logger.info("Reservation cancelled successfully for reservation ID: {}", reservationId);
    }

    public ReservationResponse requestReservationCancellation(Long reservationId, String userName) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
        
        // Verify the reservation belongs to the customer
        if (!reservation.getCustomer().getUser().getUsername().equals(userName)) {
            throw new RuntimeException("Unauthorized access to reservation");
        }
        
        // Update reservation status to CANCELLATION_REQUESTED
        reservation.setStatus("CANCELLATION_REQUESTED");
        reservationRepository.save(reservation);
        
        return mapToReservationResponse(reservation);
    }

    public List<ReservationResponse> getReservationsByDateRange(LocalDateTime fromDate, LocalDateTime toDate) {
        List<Reservation> reservations = reservationRepository.findByReservationDateTimeBetween(fromDate, toDate);
        return reservations.stream()
                .map(this::mapToReservationResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReservationResponse requestReservationUpdate(ReservationRequest request, String username) {
        logger.debug("Requesting reservation update for user: {}", username);
        
        if (request.getReservationId() == null) {
            throw new InvalidRequestException("Reservation ID is required for update requests");
        }
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new NoDataFoundException("Reservation not found with ID: " + request.getReservationId()));

        if (!reservation.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
            throw new NoDataFoundException("Reservation not found for user: " + username);
        }

        // Check if table is available for the new time
        if (isTableAvailable(request.getTableNumber(), request.getReservationDateTime())) {
            throw new InvalidRequestException("Table " + request.getTableNumber() + " is not available at the requested time");
        }

        // Update reservation with new details
        reservation.setTableNumber(request.getTableNumber());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setReservationDateTime(request.getReservationDateTime());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setStatus("UPDATE_REQUESTED");

        Reservation updatedReservation = reservationRepository.save(reservation);
        logger.info("Reservation update requested successfully for reservation ID: {}", request.getReservationId());
        
        // Send email notification to admin
        emailService.sendReservationUpdateRequest(
            "admin@biryaniworld.com",
            customer.getFirstName() + " " + customer.getLastName(),
            reservation.getId().toString(),
            reservation.getReservationDateTime().toString(),
            request.getRequestType(),
            request.getSpecialRequests()
        );

        return mapToReservationResponse(updatedReservation);
    }

    private boolean isTableAvailable(Integer tableNumber, LocalDateTime reservationDateTime) {
        LocalDateTime startTime = reservationDateTime.minusHours(2);
        LocalDateTime endTime = reservationDateTime.plusHours(2);
        
        List<Reservation> existingReservations = reservationRepository
                .findByTableNumberAndReservationDateTimeBetween(tableNumber, startTime, endTime);
        
        return !existingReservations.isEmpty();
    }

    private ReservationResponse mapToReservationResponse(Reservation reservation) {
        return ReservationResponse.builder()
                .id(reservation.getId())
                .tableNumber(reservation.getTableNumber())
                .numberOfGuests(reservation.getNumberOfGuests())
                .reservationDateTime(reservation.getReservationDateTime())
                .status(reservation.getStatus())
                .specialRequests(reservation.getSpecialRequests())
                .customerId(reservation.getCustomer().getCustomerId())
                .customerName(reservation.getCustomer().getFirstName() + " " + reservation.getCustomer().getLastName())
                .build();
    }

}
