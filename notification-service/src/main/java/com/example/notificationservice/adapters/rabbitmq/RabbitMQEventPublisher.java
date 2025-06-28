package com.example.notificationservice.adapters.rabbitmq;

import com.example.notificationservice.application.exception.UnsupportedChannelException;
import com.example.notificationservice.application.port.NotificationEventPublisherPort;
import com.example.notificationservice.client.ResendClient;
import com.example.notificationservice.client.TwilioClient;
import com.example.notificationservice.config.TwilioConfig;
import com.example.notificationservice.domain.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements NotificationEventPublisherPort {
    private static final Logger logger = LogManager.getLogger(RabbitMQEventPublisher.class);

    private final TwilioClient twilioClient;
    private final ResendClient resendClient;

    @Override
    public void publish(Notification notification) {
        logger.info("[RabbitMQEventPublisher] Publishing notification: {}", notification);

        switch (notification.getChannel().toUpperCase()) {
            case "SMS":
                twilioClient.sendSms(notification.getMessage());
                logger.info("SMS sent via Twilio to {}", notification.getRecipient());
                break;

            case "EMAIL":
                resendClient.sendEmail(notification.getRecipient(), "Notification", notification.getMessage());
                logger.info("Email sent via Resend to {}", notification.getRecipient());
                break;

            default:
                throw new UnsupportedChannelException("Unsupported notification channel: " + notification.getChannel());
        }
    }
}
