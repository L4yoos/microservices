package com.example.paymentservice.adapters.rabbitmq;

import com.example.paymentservice.domain.PaymentCompletedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RabbitMQEventPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private Exchange orderExchange;
    private ObjectMapper objectMapper;
    private RabbitMQEventPublisher publisher;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        orderExchange = mock(Exchange.class);
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        publisher = new RabbitMQEventPublisher(rabbitTemplate, orderExchange, objectMapper);
    }

    @Test
    void shouldPublishPaymentCompletedEventSuccessfully() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        double amount = 99.99;

        PaymentCompletedEvent event = new PaymentCompletedEvent(paymentId, orderId, customerId, amount, LocalDateTime.now());

        when(orderExchange.getName()).thenReturn("order.exchange");

        publisher.publish(event);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(rabbitTemplate, times(1)).convertAndSend(eq("order.exchange"), eq("payment.completed"), messageCaptor.capture());

        String sentMessage = messageCaptor.getValue();
        assertTrue(sentMessage.contains(orderId.toString()));
        assertTrue(sentMessage.contains(customerId.toString()));
        assertTrue(sentMessage.contains("99.99"));
    }

    @Test
    void shouldThrowRuntimeExceptionWhenJsonSerializationFails() {
        PaymentCompletedEvent invalidEvent = mock(PaymentCompletedEvent.class);
        when(invalidEvent.getOrderId()).thenThrow(new RuntimeException("boom")); // symulacja błędu serializacji

        publisher = new RabbitMQEventPublisher(rabbitTemplate, orderExchange, objectMapper);

        assertThrows(RuntimeException.class, () -> publisher.publish(invalidEvent));
    }
}
