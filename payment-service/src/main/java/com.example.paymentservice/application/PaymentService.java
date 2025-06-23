package com.example.paymentservice.application;

import com.example.paymentservice.application.exception.PaymentAlreadyExistsException;
import com.example.paymentservice.application.exception.PaymentNotFoundException;
import com.example.paymentservice.application.port.PaymentEventPublisherPort;
import com.example.paymentservice.application.port.PaymentRepositoryPort;
import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.PaymentCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepositoryPort paymentRepository;
    private final PaymentEventPublisherPort eventPublisher;

    public Payment processPayment(UUID orderId, UUID customerId, double amount) {
        paymentRepository.findByOrderId(orderId).ifPresent(p -> {
            throw new PaymentAlreadyExistsException("Payment already exists for order: " + orderId);
        });

        Payment payment = new Payment(orderId, customerId, amount);
        Payment saved = paymentRepository.save(payment);

        eventPublisher.publish(new PaymentCompletedEvent(
                saved.getId(),
                saved.getOrderId(),
                saved.getCustomerId(),
                saved.getAmount(),
                LocalDateTime.now()
        ));

        return saved;
    }

    public Payment getPayment(UUID id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with id: " + id));
    }
}
