package com.example.orderservice.adapters.rabbitmq;

import com.example.orderservice.application.port.OrderEventPublisherPort;
import com.example.orderservice.domain.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements OrderEventPublisherPort {
    private static final Logger logger = LogManager.getLogger(RabbitMQEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private static final String EXCHANGE_NAME = "order.exchange";
    private static final String ROUTING_KEY = "order.created";

    @Override
    public void publish(OrderCreatedEvent event) {
        logger.info("[RabbitMQEventPublisher] Publishing OrderCreatedEvent: orderId={}, customerId={}",
                event.getOrderId(), event.getCustomerId());
        try {
            String message = objectMapper.writeValueAsString(event);
            logger.debug("[RabbitMQEventPublisher] Sending message to exchange={} with routingKey={}: {}",
                    EXCHANGE_NAME, ROUTING_KEY, message);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
            logger.info("[RabbitMQEventPublisher] Successfully published OrderCreatedEvent: orderId={}",
                    event.getOrderId());
        } catch (Exception e) {
            logger.error("[RabbitMQEventPublisher] Failed to publish OrderCreatedEvent: orderId={}, error={}",
                    event.getOrderId(), e.getMessage(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}