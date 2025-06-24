package com.example.orderservice.application;

import com.example.orderservice.application.exception.InvalidOrderException;
import com.example.orderservice.application.exception.OrderNotFoundException;
import com.example.orderservice.application.port.OrderEventPublisherPort;
import com.example.orderservice.application.port.OrderRepositoryPort;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private static final Logger logger = LogManager.getLogger(OrderService.class);

    private final OrderRepositoryPort orderRepository;
    private final OrderEventPublisherPort eventPublisher;

    public Order createOrder(UUID customerId, List<String> items) {
        logger.info("[OrderService] Processing order creation: customerId={}, items={}", customerId, items);

        logger.debug("[OrderService] Creating new order for customerId={}", customerId);
        Order order = new Order(customerId, items);
        Order savedOrder = orderRepository.save(order);
        logger.info("[OrderService] Order created successfully: orderId={}", savedOrder.getId());
        eventPublisher.publish(new OrderCreatedEvent(savedOrder.getId(), customerId, items));
        logger.debug("[OrderService] OrderCreatedEvent published for orderId={}", savedOrder.getId());
        return savedOrder;
    }

    public Order getOrder(UUID orderId) {
        logger.info("[OrderService] Retrieving order: orderId={}", orderId);
        return orderRepository.findById(orderId)
                .map(order -> {
                    logger.debug("[OrderService] Order found: orderId={}", orderId);
                    return order;
                })
                .orElseThrow(() -> {
                    logger.error("[OrderService] Order not found: orderId={}", orderId);
                    return new OrderNotFoundException("Order not found: " + orderId);
                });
    }

    public void handlePaymentCompleted(UUID orderId) {
        logger.info("[OrderService] Processing payment completion: orderId={}", orderId);
        Order order = getOrder(orderId);
        logger.debug("[OrderService] Marking order as paid: orderId={}", orderId);
        order.markAsPaid();
        orderRepository.save(order);
        logger.info("[OrderService] Order marked as paid: orderId={}", orderId);
    }


    public void handlePaymentCancelled(UUID orderId) {
        logger.info("[OrderService] Processing payment cancellation: orderId={}", orderId);
        Order order = getOrder(orderId);
        logger.debug("[OrderService] Marking order as payment cancelled: orderId={}", orderId);
        order.cancel();
        orderRepository.save(order);
        logger.info("[OrderService] Order payment cancelled: orderId={}", orderId);
    }

    public void handlePaymentRefunded(UUID orderId) {
        logger.info("[OrderService] Processing payment refund: orderId={}", orderId);
        Order order = getOrder(orderId);
        logger.debug("[OrderService] Marking order as refunded: orderId={}", orderId);
        order.markAsRefunded();
        orderRepository.save(order);
        logger.info("[OrderService] Order marked as refunded: orderId={}", orderId);
    }
}