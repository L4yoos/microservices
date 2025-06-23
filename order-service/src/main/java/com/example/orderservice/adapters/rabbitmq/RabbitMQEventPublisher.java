package com.example.orderservice.adapters.rabbitmq;

import com.example.orderservice.application.port.OrderEventPublisherPort;
import com.example.orderservice.domain.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements OrderEventPublisherPort {
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private static final String EXCHANGE_NAME = "order.exchange";
    private static final String ROUTING_KEY = "order.created";

    @Override
    public void publish(OrderCreatedEvent event) {
        try {
            String message = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}