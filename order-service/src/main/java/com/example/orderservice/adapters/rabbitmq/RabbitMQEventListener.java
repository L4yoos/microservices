package com.example.orderservice.adapters.rabbitmq;

import com.example.orderservice.application.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RabbitMQEventListener {

    private static final Logger logger = LogManager.getLogger(RabbitMQEventListener.class);

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "order.payment.created.queue")
    public void handlePaymentCreated(String message) {
        logger.info("[RabbitMQEventListener] Received message on order.payment.created.queue: {}", message);
        try {
            Map<String, String> payload = objectMapper.readValue(message, Map.class);
            UUID orderId = UUID.fromString(payload.get("orderId"));
            logger.debug("[RabbitMQEventListener] Payment created for orderId={}", orderId);
            //TODO
        } catch (Exception e) {
            logger.error("[RabbitMQEventListener] Failed to process payment created: message={}, error={}",
                    message, e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "order.payment.completed.queue")
    public void handlePaymentCompleted(String message) {
        logger.info("[RabbitMQEventListener] Received message on order.payment.completed.queue: {}", message);
        try {
            Map<String, String> payload = objectMapper.readValue(message, Map.class);
            UUID orderId = UUID.fromString(payload.get("orderId"));
            logger.debug("[RabbitMQEventListener] Processing payment completed: orderId={}", orderId);
            orderService.handlePaymentCompleted(orderId);
        } catch (Exception e) {
            logger.error("[RabbitMQEventListener] Failed to process payment completed: message={}, error={}",
                    message, e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "order.payment.cancelled.queue")
    public void handlePaymentCancelled(String message) {
        logger.info("[RabbitMQEventListener] Received message on order.payment.cancelled.queue: {}", message);
        try {
            Map<String, String> payload = objectMapper.readValue(message, Map.class);
            UUID orderId = UUID.fromString(payload.get("orderId"));
            logger.debug("[RabbitMQEventListener] Processing payment cancelled: orderId={}", orderId);
            orderService.handlePaymentCancelled(orderId);
        } catch (Exception e) {
            logger.error("[RabbitMQEventListener] Failed to process payment cancelled: message={}, error={}",
                    message, e.getMessage(), e);
        }
    }

    @RabbitListener(queues = "order.payment.refunded.queue")
    public void handlePaymentRefunded(String message) {
        logger.info("[RabbitMQEventListener] Received message on order.payment.refunded.queue: {}", message);
        try {
            Map<String, String> payload = objectMapper.readValue(message, Map.class);
            UUID orderId = UUID.fromString(payload.get("orderId"));
            logger.debug("[RabbitMQEventListener] Processing payment refunded: orderId={}", orderId);
            orderService.handlePaymentRefunded(orderId);
        } catch (Exception e) {
            logger.error("[RabbitMQEventListener] Failed to process payment refunded: message={}, error={}",
                    message, e.getMessage(), e);
        }
    }
}
