package com.example.paymentservice.adapters.rest;

import com.example.paymentservice.application.PaymentService;
import com.example.paymentservice.domain.Payment;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Payment createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        return paymentService.processPayment(request.getOrderId(), request.getCustomerId(), request.getAmount());
    }

    @GetMapping("/{id}")
    public Payment getPayment(@PathVariable("id") UUID id) {
        return paymentService.getPayment(id);
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
