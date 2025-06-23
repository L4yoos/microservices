package com.example.orderservice.domain;

import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class OrderCreatedEvent {
    private final UUID orderId;
    private final UUID customerId;
    private final List<String> items;

    public OrderCreatedEvent(UUID orderId, UUID customerId, List<String> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = items;
    }
}