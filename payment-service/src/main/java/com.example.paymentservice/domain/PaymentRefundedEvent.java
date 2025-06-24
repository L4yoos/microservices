package com.example.paymentservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PaymentRefundedEvent {
    UUID paymentId;
    UUID orderId;
    UUID customerId;
    double amount;
    LocalDateTime refundedAt;

    @JsonCreator
    public PaymentRefundedEvent(
            @JsonProperty("paymentId") UUID paymentId,
            @JsonProperty("orderId") UUID orderId,
            @JsonProperty("customerId") UUID customerId,
            @JsonProperty("amount") double amount,
            @JsonProperty("refundedAt") LocalDateTime refundedAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.amount = amount;
        this.refundedAt = refundedAt;
    }
}