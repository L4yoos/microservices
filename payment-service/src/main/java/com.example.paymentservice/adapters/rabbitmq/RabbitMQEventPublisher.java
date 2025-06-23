package com.example.paymentservice.adapters.rabbitmq;

import com.example.paymentservice.application.port.PaymentEventPublisherPort;
import com.example.paymentservice.domain.PaymentCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventPublisher implements PaymentEventPublisherPort {

    private final RabbitTemplate rabbitTemplate;
    private final Exchange orderExchange;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(PaymentCompletedEvent event) {
        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(orderExchange.getName(), "payment.completed", json);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to publish PaymentCompletedEvent", e);
        }
    }
}
