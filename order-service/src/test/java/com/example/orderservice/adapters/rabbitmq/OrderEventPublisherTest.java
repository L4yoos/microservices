package com.example.orderservice.adapters.rabbitmq;

import com.example.orderservice.domain.OrderCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderEventPublisherTest {

    private RabbitTemplate rabbitTemplate;
    private ObjectMapper objectMapper;
    private RabbitMQEventPublisher publisher;

    private static final String EXCHANGE_NAME = "order.exchange";
    private static final String ROUTING_KEY = "order.created";

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        objectMapper = new ObjectMapper();
        publisher = new RabbitMQEventPublisher(rabbitTemplate, objectMapper);
    }

    @Test
    void shouldPublishOrderCreatedEvent() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        List<String> items = List.of("item1", "item2");

        OrderCreatedEvent event = new OrderCreatedEvent(orderId, customerId, items);

        publisher.publish(event);

        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(rabbitTemplate, times(1)).convertAndSend(eq(EXCHANGE_NAME), eq(ROUTING_KEY), messageCaptor.capture());

        String actualMessage = messageCaptor.getValue();
        String expectedMessage = objectMapper.writeValueAsString(event);

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void shouldThrowRuntimeExceptionWhenSerializationFails() {
        OrderCreatedEvent invalidEvent = mock(OrderCreatedEvent.class);
        when(invalidEvent.getItems()).thenThrow(new RuntimeException("Test serialization error"));

        assertThrows(RuntimeException.class, () -> publisher.publish(invalidEvent));
    }
}
