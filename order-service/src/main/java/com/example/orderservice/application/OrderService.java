package com.example.orderservice.application;

import com.example.orderservice.application.exception.InvalidOrderException;
import com.example.orderservice.application.exception.OrderNotFoundException;
import com.example.orderservice.application.port.OrderEventPublisherPort;
import com.example.orderservice.application.port.OrderRepositoryPort;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;

    public Order createOrder(UUID customerId, List<String> items) {
        if (customerId == null) {
            throw new InvalidOrderException("Customer ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderException("Order must have at least one item");
        }

        Order order = new Order(customerId, items);
        Order savedOrder = orderRepository.save(order);
        eventPublisher.publish(new OrderCreatedEvent(savedOrder.getId(), customerId, items));
        return savedOrder;
    }

    public Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found: " + orderId));
    }

    public void handlePaymentCompleted(UUID orderId) {
        Order order = getOrder(orderId);
        order.markAsPaid();
        orderRepository.save(order);
    }

    public void handleCancellationRequested(UUID orderId) {
        Order order = getOrder(orderId);
        order.cancel();
        orderRepository.save(order);
    }
}