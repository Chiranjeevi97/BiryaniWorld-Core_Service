package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.notification;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.Campaign;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.entity.authentication.User;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.CampaignStatsService;
import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.authentication.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.List;
import java.util.UUID;

@Service
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserService userService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private CampaignStatsService campaignStatsService;

    public void sendOrderConfirmation(String to, String orderNumber, String orderDetails, String phoneNumber) {
        try {
            Context context = new Context();
            context.setVariable("orderNumber", orderDetails);
            context.setVariable("orderDetails", orderDetails);
            String emailContent = templateEngine.process("order-confirmation", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Order Confirmation - Biryani World");
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");
            emailSender.send(message);
            logger.info("Order confirmation email sent to: {}", to);

            // Send SMS if phone number is provided
/*            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                smsService.sendOrderConfirmation(phoneNumber, orderDetails);
            }*/
        } catch (MailAuthenticationException e) {
            logger.error("Email authentication failed. Please check your email credentials: {}", e.getMessage());
            throw new RuntimeException("Email service configuration error. Please contact support.", e);
        } catch (MailException e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        } catch (MessagingException e) {
            logger.error("Failed to create email message: {}", e.getMessage());
            throw new RuntimeException("Failed to create email message.", e);
        }
    }

    public void sendOrderStatusUpdate(String to, String customerName, String orderStatus, 
                                    String statusMessage, String orderDetails, String estimatedTime, 
                                    String trackingLink, String phoneNumber) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("orderStatus", orderStatus);
            context.setVariable("statusMessage", statusMessage);
            context.setVariable("orderDetails", orderDetails);
            context.setVariable("estimatedTime", estimatedTime);
            context.setVariable("trackingLink", trackingLink);
            String emailContent = templateEngine.process("order-status-update", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Order Status Update - Biryani World");
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");

            emailSender.send(message);
            logger.info("Order status update email sent to: {}", to);

            // Send SMS if phone number is provided
/*            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                smsService.sendOrderStatusUpdate(phoneNumber, orderStatus, estimatedTime);
            }*/
        } catch (MailAuthenticationException e) {
            logger.error("Email authentication failed. Please check your email credentials: {}", e.getMessage());
            throw new RuntimeException("Email service configuration error. Please contact support.", e);
        } catch (MailException e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        } catch (MessagingException e) {
            logger.error("Failed to create email message: {}", e.getMessage());
            throw new RuntimeException("Failed to create email message.", e);
        }
    }

    public void sendPasswordReset(String to, String username, String resetLink) {
        try {
            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("resetLink", resetLink);
            String emailContent = templateEngine.process("password-reset", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Password Reset Request - Biryani World");
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");

            emailSender.send(message);
            logger.info("Password reset email sent to: {}", to);
        } catch (MailAuthenticationException e) {
            logger.error("Email authentication failed. Please check your email credentials: {}", e.getMessage());
            throw new RuntimeException("Email service configuration error. Please contact support.", e);
        } catch (MailException e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        } catch (MessagingException e) {
            logger.error("Failed to create email message: {}", e.getMessage());
            throw new RuntimeException("Failed to create email message.", e);
        }
    }

    public void sendCampaign(Campaign campaign) {
        logger.info("Sending campaign: {}", campaign.getTitle());
        List<User> users = userService.getAllUsers();
        int totalSent = 0;
        int bounced = 0;

        Context context = new Context();
        context.setVariable("campaignTitle", campaign.getTitle());
        context.setVariable("campaignContent", campaign.getContent());
        String emailContent = templateEngine.process("campaign", context);

        for (User user : users) {
            try {
                String trackingId = UUID.randomUUID().toString();
                String trackingPixel = generateTrackingPixel(trackingId);
                String clickTrackingLink = generateClickTrackingLink(trackingId);

                // Send email if user has email notifications enabled
                if (user.isEmailNotifications()) {
                    MimeMessage message = emailSender.createMimeMessage();
                    MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                    helper.setTo(user.getEmail());
                    helper.setSubject(campaign.getTitle());
                    helper.setText(emailContent + trackingPixel + "\n\nClick here: " + clickTrackingLink, true);
                    helper.setFrom("chiranjeevi1039@gmail.com");

                    emailSender.send(message);
                    logger.info("Campaign email sent to: {}", user.getEmail());
                }

                // Send SMS if user has SMS notifications enabled and has a phone number
/*                if (user.isSmsNotifications() && user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty()) {
                    smsService.sendCampaignNotification(user.getPhoneNumber(), campaign.getTitle(), campaign.getContent());
                    logger.info("Campaign SMS sent to: {}", user.getPhoneNumber());
                }*/

                totalSent++;
            } catch (MailAuthenticationException e) {
                logger.error("Email authentication failed. Please check your email credentials: {}", e.getMessage());
                throw new RuntimeException("Email service configuration error. Please contact support.", e);
            }  catch (MessagingException e) {
                logger.error("Failed to create email message: {}", e.getMessage());
                throw new RuntimeException("Failed to create email message.", e);
            } catch (Exception e) {
                bounced++;
                logger.error("Failed to send campaign to user: {}", user.getUsername(), e);
            }
        }

        campaignStatsService.updateCampaignStats(campaign.getId(), totalSent, 0, bounced);
    }

    private String generateTrackingPixel(String trackingId) {
        return "<img src=\"http://your-domain.com/track/" + trackingId + "\" width=\"1\" height=\"1\" />";
    }

    private String generateClickTrackingLink(String trackingId) {
        return "http://your-domain.com/click/" + trackingId;
    }

    @Scheduled(cron = "0 0 10 * * MON") // Every Monday at 10 AM
    public void sendWeeklyCampaign() {
        logger.info("Starting weekly campaign email distribution");
        List<User> users = userService.getAllUsers();
        
        Context context = new Context();
        context.setVariable("campaignTitle", "This Week's Special Offers!");
        context.setVariable("campaignContent", "Check out our latest specials and promotions!");
        String emailContent = templateEngine.process("campaign", context);

        for (User user : users) {
            try {
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(user.getEmail());
                helper.setSubject("Weekly Specials - Biryani World");
                helper.setText(emailContent, true);
                helper.setFrom("chiranjeevi1039@gmail.com");

                emailSender.send(message);
                logger.info("Campaign email sent to: {}", user.getEmail());
            } catch (MailAuthenticationException e) {
                logger.error("Email authentication failed. Please check your email credentials: {}", e.getMessage());
                throw new RuntimeException("Email service configuration error. Please contact support.", e);
            }  catch (MessagingException e) {
                logger.error("Failed to send campaign email to: {}", user.getEmail(), e);
            }
        }
    }

    public void sendReservationConfirmation(String to, String customerName, String reservationNumber, 
                                          String reservationDate, String status,
                                          int numberOfGuests) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("reservationNumber", reservationNumber);
            context.setVariable("reservationDate", reservationDate);
            context.setVariable("status", status);
            context.setVariable("numberOfGuests", numberOfGuests);
            context.setVariable("tableNumber", "Will be assigned on the day of arrival");
            
            String emailContent = templateEngine.process("reservation-confirmation", context);
            
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Table Reservation Confirmation - " + reservationNumber);
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");
            
            emailSender.send(message);
            logger.info("Reservation confirmation email sent to: {}", to);
        } catch (MailAuthenticationException e) {
            logger.error("Email authentication failed. Please check your email credentials: {}", e.getMessage());
            throw new RuntimeException("Email service configuration error. Please contact support.", e);
        } catch (MailException e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        } catch (MessagingException e) {
            logger.error("Failed to create email message: {}", e.getMessage());
            throw new RuntimeException("Failed to create email message.", e);
        }
    }

    public void sendSubscriptionConfirmation(String to, String customerName, String planName, String endDate) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("planName", planName);
            context.setVariable("endDate", endDate);
            String emailContent = templateEngine.process("subscription-confirmation", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Subscription Confirmation - Biryani World");
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");
            emailSender.send(message);
            logger.info("Subscription confirmation email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send subscription confirmation email: {}", e.getMessage());
        }
    }

    public void sendSubscriptionCancellation(String to, String customerName, String planName) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("planName", planName);
            String emailContent = templateEngine.process("subscription-cancellation", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Subscription Cancelled - Biryani World");
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");
            emailSender.send(message);
            logger.info("Subscription cancellation email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send subscription cancellation email: {}", e.getMessage());
        }
    }

    public void sendSubscriptionRenewal(String to, String customerName, String planName, String newEndDate) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("planName", planName);
            context.setVariable("newEndDate", newEndDate);
            String emailContent = templateEngine.process("subscription-renewal", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Subscription Renewed - Biryani World");
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");
            emailSender.send(message);
            logger.info("Subscription renewal email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send subscription renewal email: {}", e.getMessage());
        }
    }

    public void sendSubscriptionExpirationReminder(String to, String customerName, String planName, String endDate) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("planName", planName);
            context.setVariable("endDate", endDate);
            String emailContent = templateEngine.process("subscription-expiration-reminder", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Subscription Expiring Soon - Biryani World");
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");
            emailSender.send(message);
            logger.info("Subscription expiration reminder email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send subscription expiration reminder email: {}", e.getMessage());
        }
    }

    public void sendTierUpgrade(String to, String customerName, String newTier) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("newTier", newTier);
            String emailContent = templateEngine.process("tier-upgrade", context);

            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Congratulations! Tier Upgrade - Biryani World");
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");
            emailSender.send(message);
            logger.info("Tier upgrade email sent to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send tier upgrade email: {}", e.getMessage());
        }
    }

    public void sendReservationUpdateRequest(String to, String customerName, String reservationNumber, 
                                           String reservationDate, String requestType,
                                           String specialRequests) {
        try {
            Context context = new Context();
            context.setVariable("customerName", customerName);
            context.setVariable("reservationNumber", reservationNumber);
            context.setVariable("reservationDate", reservationDate);
            context.setVariable("requestType", requestType);
            context.setVariable("specialRequests", specialRequests);
            
            String emailContent = templateEngine.process("reservation-update-request", context);
            
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject("Reservation Update Request - " + reservationNumber);
            helper.setText(emailContent, true);
            helper.setFrom("chiranjeevi1039@gmail.com");
            
            emailSender.send(message);
            logger.info("Reservation update request email sent to: {}", to);
        } catch (MailAuthenticationException e) {
            logger.error("Email authentication failed. Please check your email credentials: {}", e.getMessage());
            throw new RuntimeException("Email service configuration error. Please contact support.", e);
        } catch (MailException | MessagingException e) {
            logger.error("Failed to send email to: {}", to, e);
            throw new RuntimeException("Failed to send email. Please try again later.", e);
        }
    }
} 