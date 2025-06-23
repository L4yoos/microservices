package com.example.paymentservice.domain;

import lombok.Getter;

import java.util.UUID;
import java.time.LocalDateTime;

@Getter
public class PaymentCompletedEvent {
    private final UUID paymentId;
    private final UUID orderId;
    private final UUID customerId;
    private final double amount;
    private final LocalDateTime completedAt;

    public PaymentCompletedEvent(UUID paymentId, UUID orderId, UUID customerId, double amount, LocalDateTime completedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.completedAt = completedAt;
    }
}


