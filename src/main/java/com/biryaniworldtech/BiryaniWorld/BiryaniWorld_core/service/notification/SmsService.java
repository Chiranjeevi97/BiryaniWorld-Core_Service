package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.service.notification;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.config.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    @Autowired
    private TwilioConfig twilioConfig;

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

    private void sendSms(String to, String message) {
        try {
            Message.creator(
                    new PhoneNumber(to),
                    new PhoneNumber(twilioConfig.getPhoneNumber()),
                    message
            ).create();
            logger.info("SMS sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Failed to send SMS to: {}", to, e);
            throw new RuntimeException("Failed to send SMS", e);
        }
    }
} 