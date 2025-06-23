package com.example.orderservice.application.port;

import com.example.orderservice.domain.OrderCreatedEvent;

public interface OrderEventPublisherPort {
    void publish(OrderCreatedEvent event);
}