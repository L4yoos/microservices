package com.example.notificationservice.adapters.rabbitmq;

import com.example.notificationservice.application.exception.UnsupportedChannelException;
import com.example.notificationservice.application.port.NotificationEventPublisherPort;
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
    private final TwilioConfig twilioConfig;
    private final ObjectMapper objectMapper;

    @Value("${twilio.phone:+48785223063}")
    String twilioPhone;

    @Value("${twilio.fromNumber:+12254522911}")
    String twilioFromPhoneNumber;

    @Override
    public void publish(Notification notification) {
        logger.info("[RabbitMQEventPublisher] Publishing NotificationRequestDto: notification={}", notification);
        if (!"SMS".equalsIgnoreCase(notification.getChannel())) {
            throw new UnsupportedChannelException("Only SMS notifications are supported.");
        }

        Message.creator(
                new PhoneNumber(twilioPhone),
                new PhoneNumber(twilioFromPhoneNumber),
                notification.getMessage()
        ).create();
        logger.info("[RabbitMQEventPublisher] Successfully published: message={}, recipient={}",
                notification.getMessage(), twilioPhone);
    }
}
