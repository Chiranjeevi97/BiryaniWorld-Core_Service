package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.MembershipTier;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.order.Orders;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.authentication.User;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.table_reservations.Reservation;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
@Builder
@Getter
@Setter
@ToString(exclude = {"orders", "reservations"})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id", nullable = false, updatable = false)
    public Long customerId;

    @Column(name = "first_name")
    public String firstName;

    @Column(name = "last_name")
    public String lastName;

    @Column(name = "email")
    public String email;

    @Column(name = "phone")
    public String phone;

    @OneToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("customer-orders")
    @Builder.Default
    private List<Orders> orders = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("customer-reservations")
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    @JsonProperty("address")
    public String address;

    @JsonProperty("subscriptionType")
    public String subscriptionType;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("subscriptionStartDate")
    public Date subscriptionStartDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("subscriptionEndDate")
    public Date subscriptionEndDate;

    @JsonProperty("isActive")
    public Boolean isActive;

    @JsonProperty("subscriptionFee")
    public Float subscriptionFee;

    @JsonProperty("rewardPoints")
    private Integer rewardPoints;

    @Enumerated(EnumType.STRING)
    @JsonProperty("membershipTier")
    private MembershipTier membershipTier;

    @JsonProperty("paymentMethod")
    public String paymentMethod;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("lastPaymentDate")
    public Date lastPaymentDate;

    @JsonProperty("notes")
    public String notes;

    public void addOrder(Orders order) {
        orders.add(order);
        order.setCustomer(this);
    }

    public void removeOrder(Orders order) {
        orders.remove(order);
        order.setCustomer(null);
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setCustomer(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setCustomer(null);
    }
}
