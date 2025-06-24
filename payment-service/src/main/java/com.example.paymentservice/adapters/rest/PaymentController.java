package com.example.paymentservice.adapters.rest;

import com.example.paymentservice.application.PaymentService;
import com.example.paymentservice.domain.Payment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private static final Logger logger = LogManager.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        logger.info("[PaymentService] Received request to create payment: orderId={}, customerId={}, amount={}",
                request.getOrderId(), request.getCustomerId(), request.getAmount());
        Payment payment = paymentService.processPayment(request.getOrderId(), request.getCustomerId(), request.getAmount());
        logger.info("[PaymentService] Successfully created payment: paymentId={}", payment.getId());
        return payment;
    }

    @PostMapping("/{id}/complete")
    @ResponseStatus(HttpStatus.OK)
    public Payment completePayment(@PathVariable("id") UUID id) {
        logger.info("[PaymentService] Received request to complete payment: paymentId={}", id);
        Payment payment = paymentService.completePayment(id);
        logger.info("[PaymentService] Successfully completed payment: paymentId={}", id);
        return payment;
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable("id") UUID id) {
        logger.info("[PaymentService] Received request to get payment: paymentId={}", id);
        Payment payment = paymentService.getPaymentOrThrow(id);
        logger.info("[PaymentService] Successfully retrieved payment: paymentId={}", id);
        return payment;
    }

    @PostMapping("/{id}/refund")
    @ResponseStatus(HttpStatus.OK)
    public Payment refundPayment(@PathVariable("id") UUID id) {
        logger.info("[PaymentService] Received request to refund payment: paymentId={}", id);
        Payment payment = paymentService.refundPayment(id);
        logger.info("[PaymentService] Successfully refunded payment: paymentId={}", id);
        return payment;
    }

    @PostMapping("/{id}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public Payment cancelPayment(@PathVariable("id") UUID id) {
        logger.info("[PaymentService] Received request to cancel payment: paymentId={}", id);
        Payment payment = paymentService.cancelPayment(id);
        logger.info("[PaymentService] Successfully cancelled payment: paymentId={}", id);
        return payment;
    }

    @Data
    public static class CreatePaymentRequest {
        @NotNull
        private UUID orderId;
        @NotNull
        private UUID customerId;
        @Min(value = 0, message = "Amount must be positive")
        private double amount;
    }
}