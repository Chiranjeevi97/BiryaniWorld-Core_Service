package com.biryaniworld.backend.service;

import com.biryaniworld.backend.config.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwilioService {

    private final TwilioConfig twilioConfig;

    public void sendSms(String to, String message) {
        try {
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    message
            ).create();
            log.info("SMS sent successfully to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send SMS to: {}", to, e);
            throw new RuntimeException("Failed to send SMS", e);
        }
    }

    public void sendOrderConfirmation(String to, String orderDetails) {
        String message = "Thank you for your order at Biryani World!\n\n" +
                "Order Details:\n" + orderDetails + "\n\n" +
                "We'll notify you when your order is ready.";
        sendSms(to, message);
    }

    public void sendOrderStatusUpdate(String to, String orderStatus, String estimatedTime) {
        String message = "Your Biryani World order status has been updated:\n\n" +
                "Status: " + orderStatus + "\n" +
                "Estimated Time: " + estimatedTime;
        sendSms(to, message);
    }

    public void sendDeliveryNotification(String to, String deliveryTime) {
        String message = "Your Biryani World order is out for delivery!\n\n" +
                "Expected delivery time: " + deliveryTime;
        sendSms(to, message);
    }

    public void sendCampaignNotification(String to, String campaignTitle, String campaignContent) {
        String message = "Biryani World Special Offer!\n\n" +
                campaignTitle + "\n\n" +
                campaignContent + "\n\n" +
                "Visit our restaurant to avail this offer!";
        sendSms(to, message);
    }
} 