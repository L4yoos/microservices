package com.example.paymentservice.adapters.persistence;

import com.example.paymentservice.application.port.PaymentRepositoryPort;
import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JpaPaymentRepositoryAdapter implements PaymentRepositoryPort {
    private final JpaPaymentRepository jpaPaymentRepository;

    @Override
    public Payment save(Payment payment) {
        return jpaPaymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaPaymentRepository.findById(id);
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return jpaPaymentRepository.findByOrderId(orderId);
    }

    @Override
    public void updateStatus(UUID paymentId, PaymentStatus status) {
        jpaPaymentRepository.updateStatus(paymentId, status);
    }
}
