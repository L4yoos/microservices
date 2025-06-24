package com.example.orderservice.application;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.orderservice.application.exception.OrderNotFoundException;
import com.example.orderservice.application.port.OrderEventPublisherPort;
import com.example.orderservice.application.port.OrderRepositoryPort;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderCreatedEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

class OrderServiceTest {

    @Mock
    private OrderRepositoryPort orderRepository;

    @Mock
    private OrderEventPublisherPort eventPublisher;

    @InjectMocks
    private OrderService orderService;

    private UUID orderId;
    private UUID customerId;
    private List<String> items;
    private Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();
        items = List.of("item1", "item2");

        // Tworzymy przykładowe zamówienie z ustalonym id (w konstruktorze domyślnym id generujemy, więc tutaj mock)
        order = mock(Order.class);
        when(order.getId()).thenReturn(orderId);
    }

    @Test
    void createOrder_shouldSaveOrderAndPublishEvent() {
        // arrange
        // zamówienie tworzone wewnątrz createOrder, więc zamockujemy orderRepository.save, żeby zwrócił nasze zamówienie
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // act
        Order created = orderService.createOrder(customerId, items);

        // assert
        assertNotNull(created);
        assertEquals(orderId, created.getId());

        verify(orderRepository).save(any(Order.class));
        verify(eventPublisher).publish(argThat(event ->
                event instanceof OrderCreatedEvent &&
                        ((OrderCreatedEvent) event).getOrderId().equals(orderId) &&
                        ((OrderCreatedEvent) event).getCustomerId().equals(customerId) &&
                        ((OrderCreatedEvent) event).getItems().equals(items)
        ));
    }

    @Test
    void getOrder_shouldReturnOrder_whenExists() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        Order result = orderService.getOrder(orderId);

        assertEquals(order, result);
        verify(orderRepository).findById(orderId);
    }

    @Test
    void getOrder_shouldThrowException_whenNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        OrderNotFoundException ex = assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(orderId));
        assertTrue(ex.getMessage().contains(orderId.toString()));

        verify(orderRepository).findById(orderId);
    }

    @Test
    void handlePaymentCompleted_shouldMarkOrderAsPaidAndSave() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.handlePaymentCompleted(orderId);

        verify(order).markAsPaid();
        verify(orderRepository).save(order);
    }

    @Test
    void handlePaymentCancelled_shouldCancelOrderAndSave() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.handlePaymentCancelled(orderId);

        verify(order).cancel();
        verify(orderRepository).save(order);
    }

    @Test
    void handlePaymentRefunded_shouldMarkOrderAsRefundedAndSave() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));

        orderService.handlePaymentRefunded(orderId);

        verify(order).markAsRefunded();
        verify(orderRepository).save(order);
    }

    @Test
    void handlePaymentCompleted_shouldThrow_whenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.handlePaymentCompleted(orderId));
    }

    @Test
    void handlePaymentCancelled_shouldThrow_whenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.handlePaymentCancelled(orderId));
    }

    @Test
    void handlePaymentRefunded_shouldThrow_whenOrderNotFound() {
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.handlePaymentRefunded(orderId));
    }
}
