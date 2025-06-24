package com.example.paymentservice.adapters.rabbitmq;

import com.example.paymentservice.domain.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class RabbitMQEventPublisherTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Exchange exchange;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private RabbitMQEventPublisher publisher;

    private final UUID paymentId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();
    private final double amount = 150.0;
    private final LocalDateTime timestamp = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(exchange.getName()).thenReturn("order.exchange");
    }

    @Test
    void publishPaymentCreatedEvent_success() throws Exception {
        PaymentCreatedEvent event = new PaymentCreatedEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenReturn("json");

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend("order.exchange", "payment.created", "json");
    }

    @Test
    void publishPaymentCreatedEvent_serializationFails_throwsRuntimeException() throws Exception {
        PaymentCreatedEvent event = new PaymentCreatedEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {});

        assertThrows(RuntimeException.class, () -> publisher.publish(event));
    }

    @Test
    void publishPaymentCompletedEvent_success() throws Exception {
        PaymentCompletedEvent event = new PaymentCompletedEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenReturn("json");

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend("order.exchange", "payment.completed", "json");
    }

    @Test
    void publishPaymentRefundedEvent_success() throws Exception {
        PaymentRefundedEvent event = new PaymentRefundedEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenReturn("json");

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend("order.exchange", "payment.refunded", "json");
    }

    @Test
    void publishPaymentCancelledEvent_success() throws Exception {
        PaymentCancelledEvent event = new PaymentCancelledEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenReturn("json");

        publisher.publish(event);

        verify(rabbitTemplate).convertAndSend("order.exchange", "payment.cancelled", "json");
    }

    @Test
    void publishPaymentCancelledEvent_serializationFails_throwsRuntimeException() throws Exception {
        PaymentCancelledEvent event = new PaymentCancelledEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenThrow(new JsonProcessingException("Serialization error") {});

        assertThrows(RuntimeException.class, () -> publisher.publish(event));
    }

    @Test
    void publishPaymentCreatedEvent_whenConvertAndSendFails_shouldThrowRuntimeException() throws Exception {
        PaymentCreatedEvent event = new PaymentCreatedEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenReturn("json");
        doThrow(new RuntimeException("Rabbit failure")).when(rabbitTemplate)
                .convertAndSend("order.exchange", "payment.created", "json");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> publisher.publish(event));
        assertEquals("Failed to publish PaymentCreatedEvent", ex.getMessage());
    }

    @Test
    void publishPaymentCompletedEvent_whenConvertAndSendFails_shouldThrowRuntimeException() throws Exception {
        PaymentCompletedEvent event = new PaymentCompletedEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenReturn("json");
        doThrow(new RuntimeException("Rabbit failure")).when(rabbitTemplate)
                .convertAndSend("order.exchange", "payment.completed", "json");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> publisher.publish(event));
        assertEquals("Failed to publish PaymentCompletedEvent", ex.getMessage());
    }

    @Test
    void publishPaymentRefundedEvent_whenConvertAndSendFails_shouldThrowRuntimeException() throws Exception {
        PaymentRefundedEvent event = new PaymentRefundedEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenReturn("json");
        doThrow(new RuntimeException("Rabbit failure")).when(rabbitTemplate)
                .convertAndSend("order.exchange", "payment.refunded", "json");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> publisher.publish(event));
        assertEquals("Failed to publish PaymentRefundedEvent", ex.getMessage());
    }

    @Test
    void publishPaymentCancelledEvent_whenConvertAndSendFails_shouldThrowRuntimeException() throws Exception {
        PaymentCancelledEvent event = new PaymentCancelledEvent(paymentId, orderId, customerId, amount, timestamp);
        when(objectMapper.writeValueAsString(event)).thenReturn("json");
        doThrow(new RuntimeException("Rabbit failure")).when(rabbitTemplate)
                .convertAndSend("order.exchange", "payment.cancelled", "json");

        RuntimeException ex = assertThrows(RuntimeException.class, () -> publisher.publish(event));
        assertEquals("Failed to publish PaymentCancelledEvent", ex.getMessage());
    }
}
