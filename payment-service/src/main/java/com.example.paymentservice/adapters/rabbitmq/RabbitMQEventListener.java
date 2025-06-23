package com.example.paymentservice.adapters.rabbitmq;

import com.example.paymentservice.application.PaymentService;
import com.example.paymentservice.domain.PaymentCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RabbitMQEventListener {

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "order.payment.completed.queue")
    public void handlePaymentCompleted(String message) {
        try {
            PaymentCompletedEvent event = objectMapper.readValue(message, PaymentCompletedEvent.class);
            paymentService.processPayment(
                    event.getOrderId(),
                    event.getCustomerId(),
                    event.getAmount()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to handle PaymentCompletedEvent", e);
        }
    }
}

