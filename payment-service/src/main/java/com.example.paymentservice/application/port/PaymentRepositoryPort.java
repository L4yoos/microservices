package com.example.paymentservice.application.port;

import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.PaymentStatus;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepositoryPort {
    Payment save(Payment payment);
    Optional<Payment> findById(UUID id);
    Optional<Payment> findByOrderId(UUID orderId);
    void updateStatus(UUID paymentId, PaymentStatus status);
}
