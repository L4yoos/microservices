package com.example.paymentservice.application;

import com.example.paymentservice.application.exception.OrderNotFoundException;
import com.example.paymentservice.application.exception.PaymentAlreadyExistsException;
import com.example.paymentservice.application.exception.PaymentNotFoundException;
import com.example.paymentservice.application.port.PaymentEventPublisherPort;
import com.example.paymentservice.application.port.PaymentRepositoryPort;
import com.example.paymentservice.domain.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentRepositoryPort repository;
    private PaymentEventPublisherPort publisher;
    private RestTemplate restTemplate;
    private PaymentService service;

    private final UUID paymentId = UUID.randomUUID();
    private final UUID orderId = UUID.randomUUID();
    private final UUID customerId = UUID.randomUUID();
    private final double amount = 150.0;

    @BeforeEach
    void setup() {
        repository = mock(PaymentRepositoryPort.class);
        publisher = mock(PaymentEventPublisherPort.class);
        restTemplate = mock(RestTemplate.class);

        service = new PaymentService(repository, publisher, restTemplate);
        service.orderServiceBaseUrl = "http://localhost:8880/orders";
    }

    @Test
    void processPayment_shouldSucceed() {
        when(repository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Payment payment = service.processPayment(orderId, customerId, amount);

        assertNotNull(payment);
        verify(publisher).publish(any(PaymentCreatedEvent.class));
    }

    @Test
    void processPayment_whenOrderDoesNotExist_shouldThrow() {
        HttpClientErrorException exception = new HttpClientErrorException(
                HttpStatus.NOT_FOUND, "Not Found");

        doThrow(exception).when(restTemplate)
                .getForEntity("http://localhost:8880/orders" + "/" + orderId, Object.class);

        assertThrows(OrderNotFoundException.class, () ->
                service.processPayment(orderId, customerId, amount));
    }


    @Test
    void processPayment_whenAlreadyExists_shouldThrow() {
        when(repository.findByOrderId(orderId)).thenReturn(Optional.of(new Payment(orderId, customerId, amount)));

        assertThrows(PaymentAlreadyExistsException.class, () ->
                service.processPayment(orderId, customerId, amount));
    }

    @Test
    void completePayment_shouldSucceed() {
        Payment payment = new Payment(orderId, customerId, amount);
        payment.setStatus(PaymentStatus.PENDING);

        when(repository.findById(payment.getId())).thenReturn(Optional.of(payment));

        Payment result = service.completePayment(payment.getId());

        assertEquals(PaymentStatus.COMPLETED, result.getStatus());
        verify(publisher).publish(any(PaymentCompletedEvent.class));
        verify(repository).updateStatus(payment.getId(), PaymentStatus.COMPLETED);
    }

    @Test
    void completePayment_whenNotFound_shouldThrow() {
        when(repository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () ->
                service.completePayment(paymentId));
    }

    @Test
    void refundPayment_shouldSucceed() {
        Payment payment = new Payment(orderId, customerId, amount);
        payment.setStatus(PaymentStatus.COMPLETED);

        when(repository.findById(payment.getId())).thenReturn(Optional.of(payment));

        Payment result = service.refundPayment(payment.getId());

        assertEquals(PaymentStatus.REFUNDED, result.getStatus());
        verify(publisher).publish(any(PaymentRefundedEvent.class));
        verify(repository).updateStatus(payment.getId(), PaymentStatus.REFUNDED);
    }

    @Test
    void refundPayment_whenNotFound_shouldThrow() {
        when(repository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () ->
                service.refundPayment(paymentId));
    }

    @Test
    void cancelPayment_shouldSucceed() {
        Payment payment = new Payment(orderId, customerId, amount);
        payment.setStatus(PaymentStatus.PENDING);

        when(repository.findById(payment.getId())).thenReturn(Optional.of(payment));

        Payment result = service.cancelPayment(payment.getId());

        assertEquals(PaymentStatus.CANCELLED, result.getStatus());
        verify(publisher).publish(any(PaymentCancelledEvent.class));
        verify(repository).updateStatus(payment.getId(), PaymentStatus.CANCELLED);
    }

    @Test
    void cancelPayment_whenNotFound_shouldThrow() {
        when(repository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () ->
                service.cancelPayment(paymentId));
    }

    @Test
    void getPaymentOrThrow_shouldReturnPayment() {
        Payment payment = new Payment(orderId, customerId, amount);
        when(repository.findById(payment.getId())).thenReturn(Optional.of(payment));

        Payment result = service.getPaymentOrThrow(payment.getId());
        assertEquals(payment, result);
    }

    @Test
    void getPaymentOrThrow_whenMissing_shouldThrow() {
        when(repository.findById(paymentId)).thenReturn(Optional.empty());

        assertThrows(PaymentNotFoundException.class, () ->
                service.getPaymentOrThrow(paymentId));
    }
}
