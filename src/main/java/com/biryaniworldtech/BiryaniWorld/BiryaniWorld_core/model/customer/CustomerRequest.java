package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.MembershipTier;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.order.Orders;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "orders")
public class CustomerRequest {

    @JsonProperty("id")
    public Long id;

    @JsonProperty("firstName")
    @NotBlank(message = "First name is required")
    public String firstName;

    @NotBlank(message = "Last name is required")
    @JsonProperty("lastName")
    public String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    @JsonProperty("email")
    public String email;

    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    @NotBlank(message = "Phone number is required")
    @JsonProperty("phone")
    public String phone;

    @NotBlank(message = "Address is required")
    @JsonProperty("address")
    public String address;

    @NotBlank(message = "Subscription type is required")
    @JsonProperty("subscriptionType")
    public String subscriptionType;

    @Future(message = "Subscription must start in the future")
    @NotNull(message = "Subscription start date is required")
    @JsonProperty("subscriptionStartDate")
    public Date subscriptionStartDate;

    @Future(message = "Subscription must end in the future")
    @NotNull(message = "Subscription end date is required")
    @JsonProperty("subscriptionEndDate")
    public Date subscriptionEndDate;

    @NotNull(message = "Active status is required")
    @JsonProperty("isActive")
    public Boolean isActive;

    @NotNull(message = "Subscription fee is required")
    @Positive(message = "Subscription fee must be positive")
    @JsonProperty("subscriptionFee")
    public Float subscriptionFee;

    private Integer rewardPoints;

    @Enumerated(EnumType.STRING)
    private MembershipTier membershipTier;

    @NotBlank(message = "Payment method is required")
    @JsonProperty("paymentMethod")
    public String paymentMethod;

    @PastOrPresent(message = "Date of lastPayment cannot be in the future")
    @NotNull(message = "Last payment date is required")
    @JsonProperty("lastPaymentDate")
    public Date lastPaymentDate;

    @JsonProperty("orders")
    @Nullable
    public List<Orders> orders;

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    @JsonProperty("notes")
    public String notes;

}
