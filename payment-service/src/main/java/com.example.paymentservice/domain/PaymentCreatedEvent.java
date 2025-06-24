package com.example.paymentservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PaymentCreatedEvent {
    UUID paymentId;
    UUID orderId;
    UUID customerId;
    double amount;
    LocalDateTime createdAt;

    @JsonCreator
    public PaymentCreatedEvent(
            @JsonProperty("paymentId") UUID paymentId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("customerId") UUID customerId,
            @JsonProperty("amount") double amount,
            @JsonProperty("createdAt") LocalDateTime createdAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.createdAt = createdAt;
    }
}