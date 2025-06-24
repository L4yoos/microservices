package com.example.paymentservice.application;

import com.example.paymentservice.application.exception.OrderNotFoundException;
import com.example.paymentservice.application.exception.PaymentAlreadyExistsException;
import com.example.paymentservice.application.exception.PaymentNotFoundException;
import com.example.paymentservice.application.port.PaymentEventPublisherPort;
import com.example.paymentservice.application.port.PaymentRepositoryPort;
import com.example.paymentservice.domain.*;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final Logger logger = LogManager.getLogger(PaymentService.class);

    private final PaymentRepositoryPort paymentRepository;
    private final PaymentEventPublisherPort eventPublisher;
    private final RestTemplate restTemplate;

    @Value("${order-service.url:http://localhost:8880/orders}")
    String orderServiceBaseUrl;

    @Transactional
    public Payment processPayment(UUID orderId, UUID customerId, double amount) {
        logger.info("[PaymentService] Processing payment: orderId={}, customerId={}, amount={}", orderId, customerId, amount);

        validateOrderExists(orderId);

        paymentRepository.findByOrderId(orderId).ifPresent(p -> {
            logger.error("[PaymentService] Payment already exists for order: orderId={}", orderId);
            throw new PaymentAlreadyExistsException("Payment already exists for order: " + orderId);
        });

        Payment payment = new Payment(orderId, customerId, amount);
        Payment saved = paymentRepository.save(payment);
        logger.info("[PaymentService] Saved payment: paymentId={}", saved.getId());

        Object event = mapToEvent(payment, PaymentCreatedEvent.class);
        eventPublisher.publish((PaymentCreatedEvent) event);
        logger.info("[PaymentService] Published PaymentCreatedEvent: paymentId={}", payment.getId());

        return saved;
    }

    @Transactional
    public Payment completePayment(UUID paymentId) {
        logger.info("[PaymentService] Completing payment: paymentId={}", paymentId);
        Payment payment = getPaymentOrThrow(paymentId);
        payment.markAsCompleted();
        paymentRepository.updateStatus(payment.getId(), PaymentStatus.COMPLETED);
        logger.info("[PaymentService] Updated payment status to COMPLETED: paymentId={}", payment.getId());

        Object event = mapToEvent(payment, PaymentCompletedEvent.class);
        eventPublisher.publish((PaymentCompletedEvent) event);
        logger.info("[PaymentService] Published PaymentCompletedEvent: paymentId={}", payment.getId());

        return payment;
    }

    @Transactional
    public Payment refundPayment(UUID paymentId) {
        logger.info("[PaymentService] Refunding payment: paymentId={}", paymentId);
        Payment payment = getPaymentOrThrow(paymentId);
        payment.markAsRefunded();
        paymentRepository.updateStatus(payment.getId(), PaymentStatus.REFUNDED);
        logger.info("[PaymentService] Updated payment status to REFUNDED: paymentId={}", payment.getId());

        Object event = mapToEvent(payment, PaymentRefundedEvent.class);
        eventPublisher.publish((PaymentRefundedEvent) event);
        logger.info("[PaymentService] Published PaymentRefundedEvent: paymentId={}", payment.getId());

        return payment;
    }

    @Transactional
    public Payment cancelPayment(UUID paymentId) {
        logger.info("[PaymentService] Cancelling payment: paymentId={}", paymentId);
        Payment payment = getPaymentOrThrow(paymentId);
        payment.markAsCancelled();
        paymentRepository.updateStatus(payment.getId(), PaymentStatus.CANCELLED);
        logger.info("[PaymentService] Updated payment status to CANCELLED: paymentId={}", payment.getId());

        Object event = mapToEvent(payment, PaymentCancelledEvent.class);
        eventPublisher.publish((PaymentCancelledEvent) event);
        logger.info("[PaymentService] Published PaymentCancelledEvent: paymentId={}", payment.getId());

        return payment;
    }

    public Payment getPaymentOrThrow(UUID paymentId) {
        logger.debug("[PaymentService] Retrieving payment: paymentId={}", paymentId);
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.error("[PaymentService] Payment not found: paymentId={}", paymentId);
                    return new PaymentNotFoundException("Payment not found with id: " + paymentId);
                });
    }

    private Object mapToEvent(Payment payment, Class<?> eventClass) {
        LocalDateTime now = LocalDateTime.now();
        if (eventClass.equals(PaymentCreatedEvent.class)) {
            return new PaymentCreatedEvent(payment.getId(), payment.getOrderId(), payment.getCustomerId(), payment.getAmount(), now);
        } else if (eventClass.equals(PaymentCompletedEvent.class)) {
            return new PaymentCompletedEvent(payment.getId(), payment.getOrderId(), payment.getCustomerId(), payment.getAmount(), now);
        } else if (eventClass.equals(PaymentRefundedEvent.class)) {
            return new PaymentRefundedEvent(payment.getId(), payment.getOrderId(), payment.getCustomerId(), payment.getAmount(), now);
        } else if (eventClass.equals(PaymentCancelledEvent.class)) {
            return new PaymentCancelledEvent(payment.getId(), payment.getOrderId(), payment.getCustomerId(), payment.getAmount(), now);
        } else {
            logger.error("[PaymentService] Unsupported event class: {}", eventClass);
            throw new IllegalArgumentException("Unsupported event class: " + eventClass);
        }
    }

    private void validateOrderExists(UUID orderId) {
        String url = orderServiceBaseUrl + "/" + orderId;
        logger.debug("[PaymentService] Validating order existence: orderId={}, url={}", orderId, url);
        try {
            restTemplate.getForEntity(url, Object.class);
            logger.debug("[PaymentService] Order validated successfully: orderId={}", orderId);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                logger.error("[PaymentService] Order not found: orderId={}", orderId, e);
                throw new OrderNotFoundException("Order not found with id: " + orderId);
            }
            throw e;
        }
    }
}
