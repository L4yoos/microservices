package com.example.paymentservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.util.UUID;
import java.time.LocalDateTime;

@Getter
public class PaymentCompletedEvent {
    UUID paymentId;
    UUID orderId;
    UUID customerId;
    double amount;
    LocalDateTime completedAt;

    @JsonCreator
    public PaymentCompletedEvent(
            @JsonProperty("paymentId") UUID paymentId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("customerId") UUID customerId,
            @JsonProperty("amount") double amount,
            @JsonProperty("completedAt") LocalDateTime completedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.completedAt = completedAt;
    }
}


