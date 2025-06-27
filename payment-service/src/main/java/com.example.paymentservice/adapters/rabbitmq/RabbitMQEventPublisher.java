package com.example.paymentservice.adapters.rabbitmq;

import com.example.paymentservice.application.port.PaymentEventPublisherPort;
import com.example.paymentservice.domain.PaymentCancelledEvent;
import com.example.paymentservice.domain.PaymentCompletedEvent;
import com.example.paymentservice.domain.PaymentCreatedEvent;
import com.example.paymentservice.domain.PaymentRefundedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements PaymentEventPublisherPort {

    private static final Logger logger = LogManager.getLogger(RabbitMQEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;
    private final Exchange orderExchange;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(PaymentCreatedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(orderExchange.getName(), "payment.created", json);
            logger.info("Successfully published PaymentCreatedEvent: paymentId={}", event.getPaymentId());
        } catch (Exception e) {
            logger.error("Failed to publish PaymentCreatedEvent: paymentId={}", event.getPaymentId(), e);
            throw new RuntimeException("Failed to publish PaymentCreatedEvent", e);
        }
    }

    @Override
    public void publish(PaymentCompletedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(orderExchange.getName(), "payment.completed", json);
            logger.info("Successfully published PaymentCompletedEvent: paymentId={}", event.getPaymentId());
            rabbitTemplate.convertAndSend(orderExchange.getName(), "notification.payment.completed", json);
            logger.info("Successfully publish to notification.payment.completed: paymentId={}", event.getPaymentId());
        } catch (Exception e) {
            logger.error("Failed to publish PaymentCompletedEvent: paymentId={}", event.getPaymentId(), e);
            throw new RuntimeException("Failed to publish PaymentCompletedEvent", e);
        }
    }

    @Override
    public void publish(PaymentRefundedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(orderExchange.getName(), "payment.refunded", json);
            logger.info("Successfully published PaymentRefundedEvent: {}", event.getPaymentId());
        } catch (Exception e) {
            logger.error("Failed to publish PaymentRefundedEvent: {}", event.getPaymentId(), e);
            throw new RuntimeException("Failed to publish PaymentRefundedEvent", e);
        }
    }

    @Override
    public void publish(PaymentCancelledEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(orderExchange.getName(), "payment.cancelled", json);
            logger.info("Successfully published PaymentCancelledEvent: {}", event.getPaymentId());
        } catch (Exception e) {
            logger.error("Failed to publish PaymentCancelledEvent: {}", event.getPaymentId(), e);
            throw new RuntimeException("Failed to publish PaymentCancelledEvent", e);
        }
    }
}
