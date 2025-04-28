package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.table_reservations;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.table_reservations.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByCustomer_CustomerId(Long customerId);
    List<Reservation> findByReservationDateTimeBetween(LocalDateTime fromDate, LocalDateTime toDate);
    List<Reservation> findByTableNumberAndReservationDateTimeBetween(Integer tableNumber, LocalDateTime start, LocalDateTime end);
    List<Reservation> findByStatus(String status);
}
