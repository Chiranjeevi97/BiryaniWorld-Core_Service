package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.customer.loyalty;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.authentication.User;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.Customer;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.customer.loyalty.*;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.InvalidRequestException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.exception.NoDataFoundException;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.model.customer.loyalty.*;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.CustomerRepository;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.repository.customer.loyalty.*;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.authentication.UserService;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.notification.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class LoyaltyService {
    private static final Logger logger = LoggerFactory.getLogger(LoyaltyService.class);

    @Autowired
    private LoyaltyAccountRepository loyaltyAccountRepository;

    @Autowired
    private SubscriptionPlanRepository subscriptionPlanRepository;

    @Autowired
    private LoyaltyTransactionRepository loyaltyTransactionRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Transactional
    public LoyaltyDashboardResponse getLoyaltyDashboard(String username) {
        logger.debug("Fetching loyalty dashboard for user: {}", username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository.findByCustomer_CustomerId(customer.getCustomerId())
                .orElseThrow(() -> new NoDataFoundException("Loyalty account not found for customer"));

        List<LoyaltyTransaction> recentTransactions = loyaltyTransactionRepository
                .findByLoyaltyAccountIdOrderByTransactionDateDesc(loyaltyAccount.getId());

        return LoyaltyDashboardResponse.builder()
                .pointsBalance(loyaltyAccount.getPointsBalance())
                .lifetimePoints(loyaltyAccount.getLifetimePoints())
                .tier(loyaltyAccount.getTier())
                .referralCode(loyaltyAccount.getReferralCode())
                .referralCount(loyaltyAccount.getReferralCount())
                .subscriptionPlan(loyaltyAccount.getSubscriptionPlan())
                .subscriptionStatus(loyaltyAccount.getSubscriptionStatus())
                .subscriptionEndDate(loyaltyAccount.getSubscriptionEndDate())
                .autoRenew(loyaltyAccount.getAutoRenew())
                .recentTransactions(recentTransactions.stream()
                        .map(this::mapToTransactionResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public SubscriptionResponse subscribeToPlan(Long planId, String username) {
        logger.debug("Subscribing user {} to plan {}", username, planId);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        SubscriptionPlan plan = subscriptionPlanRepository.findById(planId)
                .orElseThrow(() -> new NoDataFoundException("Subscription plan not found"));

        if (!plan.getIsActive()) {
            throw new InvalidRequestException("This subscription plan is no longer active");
        }

        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository.findByCustomer_CustomerId(customer.getCustomerId())
                .orElseGet(() -> createLoyaltyAccount(customer));

        LocalDateTime now = LocalDateTime.now();
        loyaltyAccount.setSubscriptionPlan(plan);
        loyaltyAccount.setSubscriptionStartDate(now);
        loyaltyAccount.setSubscriptionEndDate(now.plusMonths(plan.getDurationMonths()));
        loyaltyAccount.setSubscriptionStatus("ACTIVE");

        LoyaltyAccount savedAccount = loyaltyAccountRepository.save(loyaltyAccount);
        
        // Send subscription confirmation email
        emailService.sendSubscriptionConfirmation(
            customer.getEmail(),
            customer.getFirstName(),
            plan.getName(),
            savedAccount.getSubscriptionEndDate().toString()
        );

        return mapToSubscriptionResponse(savedAccount);
    }

    @Transactional
    public void toggleAutoRenew(String username) {
        logger.debug("Toggling auto-renew for user: {}", username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository.findByCustomer_CustomerId(customer.getCustomerId())
                .orElseThrow(() -> new NoDataFoundException("Loyalty account not found for customer"));

        loyaltyAccount.setAutoRenew(!loyaltyAccount.getAutoRenew());
        loyaltyAccountRepository.save(loyaltyAccount);
    }

    @Transactional
    public void cancelSubscription(String username) {
        logger.debug("Cancelling subscription for user: {}", username);
        
        User user = userService.getUserByUsername(username);
        Customer customer = user.getCustomer();
        
        if (customer == null) {
            throw new NoDataFoundException("Customer profile not found for user: " + username);
        }

        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository.findByCustomer_CustomerId(customer.getCustomerId())
                .orElseThrow(() -> new NoDataFoundException("Loyalty account not found for customer"));

        loyaltyAccount.setSubscriptionStatus("CANCELLED");
        loyaltyAccount.setAutoRenew(false);
        loyaltyAccountRepository.save(loyaltyAccount);

        // Send subscription cancellation email
        emailService.sendSubscriptionCancellation(
            customer.getEmail(),
            customer.getFirstName(),
            loyaltyAccount.getSubscriptionPlan().getName()
        );
    }

    @Transactional
    public void addPoints(Long customerId, Integer points, String description, String referenceId) {
        logger.debug("Adding {} points to customer {}", points, customerId);
        
        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new NoDataFoundException("Loyalty account not found for customer"));

        // Apply points multiplier if customer has an active subscription
        if (loyaltyAccount.getSubscriptionPlan() != null && 
            "ACTIVE".equals(loyaltyAccount.getSubscriptionStatus())) {
            points = (int) (points * loyaltyAccount.getSubscriptionPlan().getRewardPointsMultiplier());
        }

        loyaltyAccount.setPointsBalance(loyaltyAccount.getPointsBalance() + points);
        loyaltyAccount.setLifetimePoints(loyaltyAccount.getLifetimePoints() + points);
        
        // Check for tier upgrade
        checkAndUpdateTier(loyaltyAccount);
        
        loyaltyAccountRepository.save(loyaltyAccount);

        // Record transaction
        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .loyaltyAccount(loyaltyAccount)
                .points(points)
                .transactionType("EARNED")
                .description(description)
                .referenceId(referenceId)
                .build();
        loyaltyTransactionRepository.save(transaction);
    }

    @Transactional
    public void redeemPoints(Long customerId, Integer points, String description) {
        logger.debug("Redeeming {} points from customer {}", points, customerId);
        
        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository.findByCustomer_CustomerId(customerId)
                .orElseThrow(() -> new NoDataFoundException("Loyalty account not found for customer"));

        if (loyaltyAccount.getPointsBalance() < points) {
            throw new InvalidRequestException("Insufficient points balance");
        }

        loyaltyAccount.setPointsBalance(loyaltyAccount.getPointsBalance() - points);
        loyaltyAccountRepository.save(loyaltyAccount);

        // Record transaction
        LoyaltyTransaction transaction = LoyaltyTransaction.builder()
                .loyaltyAccount(loyaltyAccount)
                .points(-points)
                .transactionType("REDEEMED")
                .description(description)
                .build();
        loyaltyTransactionRepository.save(transaction);
    }

    @Transactional
    public void processReferral(String referralCode, Long newCustomerId) {
        logger.debug("Processing referral {} for new customer {}", referralCode, newCustomerId);
        
        LoyaltyAccount referrerAccount = loyaltyAccountRepository.findByReferralCode(referralCode)
                .orElseThrow(() -> new NoDataFoundException("Invalid referral code"));

        // Add points to referrer
        addPoints(referrerAccount.getCustomer().getCustomerId(), 100, "Referral bonus", null);
        
        // Increment referral count
        referrerAccount.setReferralCount(referrerAccount.getReferralCount() + 1);
        loyaltyAccountRepository.save(referrerAccount);

        // Add points to new customer
        addPoints(newCustomerId, 50, "Welcome bonus", null);
    }

    @Scheduled(cron = "0 0 0 * * ?") // Run at midnight every day
    @Transactional
    public void processExpiringSubscriptions() {
        logger.debug("Processing expiring subscriptions");
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysFromNow = now.plusDays(30);
        
        List<LoyaltyAccount> expiringAccounts = loyaltyAccountRepository
                .findBySubscriptionEndDateBetween(now, thirtyDaysFromNow);

        for (LoyaltyAccount account : expiringAccounts) {
            if (account.getAutoRenew()) {
                // Auto-renew subscription
                LocalDateTime newEndDate = account.getSubscriptionEndDate()
                        .plusMonths(account.getSubscriptionPlan().getDurationMonths());
                account.setSubscriptionEndDate(newEndDate);
                loyaltyAccountRepository.save(account);

                // Send renewal confirmation email
                emailService.sendSubscriptionRenewal(
                    account.getCustomer().getEmail(),
                    account.getCustomer().getFirstName(),
                    account.getSubscriptionPlan().getName(),
                    newEndDate.toString()
                );
            } else {
                // Send expiration reminder email
                emailService.sendSubscriptionExpirationReminder(
                    account.getCustomer().getEmail(),
                    account.getCustomer().getFirstName(),
                    account.getSubscriptionPlan().getName(),
                    account.getSubscriptionEndDate().toString()
                );
            }
        }
    }

    private LoyaltyAccount createLoyaltyAccount(Customer customer) {
        return LoyaltyAccount.builder()
                .customer(customer)
                .pointsBalance(0)
                .lifetimePoints(0)
                .tier(MembershipTier.BRONZE)
                .referralCode(generateReferralCode())
                .referralCount(0)
                .lastTierUpdate(LocalDateTime.now())
                .autoRenew(false)
                .subscriptionStatus("INACTIVE")
                .build();
    }

    private void checkAndUpdateTier(LoyaltyAccount account) {
        MembershipTier newTier = determineTier(account.getLifetimePoints());
        if (newTier != account.getTier()) {
            account.setTier(newTier);
            account.setLastTierUpdate(LocalDateTime.now());
            loyaltyAccountRepository.save(account);

            // Send tier upgrade email
            emailService.sendTierUpgrade(
                account.getCustomer().getEmail(),
                account.getCustomer().getFirstName(),
                newTier.toString()
            );
        }
    }

    private MembershipTier determineTier(Integer lifetimePoints) {
        if (lifetimePoints >= 10000) {
            return MembershipTier.PLATINUM;
        } else if (lifetimePoints >= 5000) {
            return MembershipTier.GOLD;
        } else if (lifetimePoints >= 1000) {
            return MembershipTier.SILVER;
        } else {
            return MembershipTier.BRONZE;
        }
    }

    private String generateReferralCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private LoyaltyTransactionResponse mapToTransactionResponse(LoyaltyTransaction transaction) {
        return LoyaltyTransactionResponse.builder()
                .id(transaction.getId())
                .points(transaction.getPoints())
                .transactionType(transaction.getTransactionType())
                .description(transaction.getDescription())
                .referenceId(transaction.getReferenceId())
                .transactionDate(transaction.getTransactionDate())
                .build();
    }

    private SubscriptionResponse mapToSubscriptionResponse(LoyaltyAccount account) {
        return SubscriptionResponse.builder()
                .planId(account.getSubscriptionPlan().getId())
                .planName(account.getSubscriptionPlan().getName())
                .startDate(account.getSubscriptionStartDate())
                .endDate(account.getSubscriptionEndDate())
                .status(account.getSubscriptionStatus())
                .autoRenew(account.getAutoRenew())
                .build();
    }
}
