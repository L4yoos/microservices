package com.example.orderservice.adapters.rabbitmq;

import com.example.orderservice.application.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RabbitMQEventListener {
    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "order.payment.completed.queue")
    public void handlePaymentCompleted(String message) throws Exception {
        System.out.println("Received message on order.payment.completed.queue: " + message);
        Map<String, String> payload = objectMapper.readValue(message, Map.class);
        UUID orderId = UUID.fromString(payload.get("orderId"));
        orderService.handlePaymentCompleted(orderId);
    }

    @RabbitListener(queues = "order.cancellation.requested.queue")
    public void handleCancellationRequested(String message) throws Exception {
        Map<String, String> payload = objectMapper.readValue(message, Map.class);
        UUID orderId = UUID.fromString(payload.get("orderId"));
        orderService.handleCancellationRequested(orderId);
    }
}