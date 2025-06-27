package com.example.notificationservice.adapters.rabbitmq;

import com.example.notificationservice.application.NotificationService;
import com.example.notificationservice.domain.NotificationRequestDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RabbitMQEventListener {

    private static final Logger logger = LogManager.getLogger(RabbitMQEventListener.class);

    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    @Value("${twilio.phone:+48785223063}")
    String twilioPhone;

    @RabbitListener(queues = "notification.payment.completed.queue")
    public void handlePaymentCompleted(String payMessage) throws JsonProcessingException {
        logger.info("[RabbitMQEventListener] Received message on notification.payment.completed.queue: {}", payMessage);
        try {
            Map<String, Object> payload = objectMapper.readValue(payMessage, Map.class);

            UUID orderId = UUID.fromString((String) payload.get("orderId"));
            double amount = ((Number) payload.get("amount")).doubleValue();

            String message = String.format(
                    "Twoja płatność za zamówienie %s na kwotę %.2f została zakończona.",
                    orderId, amount
            );

            NotificationRequestDto request = new NotificationRequestDto(twilioPhone, message, "SMS");
            notificationService.send(request);
        } catch (Exception e) {
            logger.error("[RabbitMQEventListener] Failed to process payment created: message={}, error={}",
                    payMessage, e.getMessage(), e);
        }
    }
}
