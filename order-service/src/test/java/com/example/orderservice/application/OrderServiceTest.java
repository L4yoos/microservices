package com.example.orderservice.application;

import com.example.orderservice.application.exception.InvalidOrderException;
import com.example.orderservice.application.exception.OrderNotFoundException;
import com.example.orderservice.application.port.OrderEventPublisherPort;
import com.example.orderservice.application.port.OrderRepositoryPort;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderCreatedEvent;
import com.example.orderservice.domain.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderRepositoryPort orderRepository;
    private OrderEventPublisherPort eventPublisher;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = mock(OrderRepositoryPort.class);
        eventPublisher = mock(OrderEventPublisherPort.class);
        orderService = new OrderService(orderRepository, eventPublisher);
    }

    @Test
    void shouldCreateOrderAndPublishEvent() {
        UUID customerId = UUID.randomUUID();
        List<String> items = List.of("item1", "item2");

        Order created = new Order(customerId, items);
        when(orderRepository.save(any(Order.class))).thenReturn(created);

        Order result = orderService.createOrder(customerId, items);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(any());
    }

    @Test
    void shouldReturnOrderWhenExists() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(UUID.randomUUID(), List.of("test"));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder(orderId);

        assertEquals(order, result);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        Exception ex = assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrder(orderId);
        });

        assertTrue(ex.getMessage().contains(orderId.toString()));
    }

    @Test
    void shouldMarkOrderAsPaid() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(UUID.randomUUID(), List.of("a"));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.handlePaymentCompleted(orderId);

        assertEquals(OrderStatus.PAID, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldCancelOrder() {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(UUID.randomUUID(), List.of("a"));
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.handleCancellationRequested(orderId);

        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        verify(orderRepository).save(order);
    }

    @Test
    void shouldPublishCorrectOrderCreatedEvent() {
        UUID customerId = UUID.randomUUID();
        List<String> items = List.of("item1", "item2");

        Order order = new Order(customerId, items);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.createOrder(customerId, items);

        ArgumentCaptor<OrderCreatedEvent> captor = ArgumentCaptor.forClass(OrderCreatedEvent.class);
        verify(eventPublisher).publish(captor.capture());

        OrderCreatedEvent event = captor.getValue();
        assertEquals(order.getId(), event.getOrderId());
        assertEquals(customerId, event.getCustomerId());
        assertEquals(items, event.getItems());
    }

    @Test
    void shouldNotMarkAsPaidIfAlreadyPaid() {
        Order order = new Order(UUID.randomUUID(), List.of("test"));
        order.markAsPaid(); // already paid

        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            orderService.handlePaymentCompleted(orderId);
        });

        assertEquals("Order is already paid", ex.getMessage());
    }

    @Test
    void shouldThrowWhenCancellingPaidOrder() {
        Order order = new Order(UUID.randomUUID(), List.of("test"));
        order.markAsPaid();

        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            orderService.handleCancellationRequested(orderId);
        });

        assertEquals("Cannot cancel a paid order", ex.getMessage());
    }

    @Test
    void shouldThrowWhenAlreadyCancelled() {
        Order order = new Order(UUID.randomUUID(), List.of("test"));
        order.cancel();

        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            orderService.handleCancellationRequested(orderId);
        });

        assertEquals("Order is already canceled", ex.getMessage());
    }

    @Test
    void shouldThrowOnCreateOrderWithEmptyItems() {
        UUID customerId = UUID.randomUUID();
        List<String> items = List.of();

        Exception ex = assertThrows(InvalidOrderException.class, () -> {
            orderService.createOrder(customerId, items);
        });

        assertTrue(ex.getMessage().toLowerCase().contains("item"));
    }

    @Test
    void shouldThrowOnCreateOrderWithNullCustomer() {
        Exception ex = assertThrows(InvalidOrderException.class, () -> {
            orderService.createOrder(null, List.of("item1"));
        });

        assertNotNull(ex);
    }
}
