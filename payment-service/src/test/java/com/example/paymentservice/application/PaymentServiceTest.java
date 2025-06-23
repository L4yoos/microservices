package com.example.paymentservice.application;

import com.example.paymentservice.application.exception.PaymentAlreadyExistsException;
import com.example.paymentservice.application.exception.PaymentNotFoundException;
import com.example.paymentservice.application.port.PaymentEventPublisherPort;
import com.example.paymentservice.application.port.PaymentRepositoryPort;
import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.PaymentCompletedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    @Mock
    private PaymentRepositoryPort paymentRepository;

    @Mock
    private PaymentEventPublisherPort eventPublisher;

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processPayment_ShouldSaveAndPublishEvent_WhenPaymentDoesNotExist() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        double amount = 150.0;

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        UUID paymentId = UUID.randomUUID();
        Payment savedPayment = new Payment(orderId, customerId, amount) {
            @Override
            public UUID getId() {
                return paymentId;
            }
        };
        when(paymentRepository.save(any(Payment.class))).thenReturn(savedPayment);

        Payment result = paymentService.processPayment(orderId, customerId, amount);

        assertNotNull(result);
        assertEquals(paymentId, result.getId());
        assertEquals(orderId, result.getOrderId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(amount, result.getAmount());

        verify(paymentRepository).findByOrderId(orderId);
        verify(paymentRepository).save(paymentCaptor.capture());
        verify(eventPublisher).publish(any(PaymentCompletedEvent.class));

        Payment capturedPayment = paymentCaptor.getValue();
        assertEquals(orderId, capturedPayment.getOrderId());
        assertEquals(customerId, capturedPayment.getCustomerId());
        assertEquals(amount, capturedPayment.getAmount());
    }

    @Test
    void processPayment_ShouldThrow_WhenPaymentAlreadyExists() {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        double amount = 100.0;

        Payment existingPayment = new Payment(orderId, customerId, amount);

        when(paymentRepository.findByOrderId(orderId)).thenReturn(Optional.of(existingPayment));

        PaymentAlreadyExistsException exception = assertThrows(PaymentAlreadyExistsException.class,
                () -> paymentService.processPayment(orderId, customerId, amount));

        assertTrue(exception.getMessage().contains(orderId.toString()));

        verify(paymentRepository).findByOrderId(orderId);
        verify(paymentRepository, never()).save(any());
        verify(eventPublisher, never()).publish(any());
    }

    @Test
    void getPayment_ShouldReturnPayment_WhenExists() {
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        double amount = 120.0;

        Payment payment = new Payment(orderId, customerId, amount) {
            @Override
            public UUID getId() {
                return paymentId;
            }
        };

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

        Payment result = paymentService.getPayment(paymentId);

        assertNotNull(result);
        assertEquals(paymentId, result.getId());
        assertEquals(orderId, result.getOrderId());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(amount, result.getAmount());

        verify(paymentRepository).findById(paymentId);
    }

    @Test
    void getPayment_ShouldThrow_WhenNotFound() {
        UUID paymentId = UUID.randomUUID();

        when(paymentRepository.findById(paymentId)).thenReturn(Optional.empty());

        PaymentNotFoundException exception = assertThrows(PaymentNotFoundException.class,
                () -> paymentService.getPayment(paymentId));

        assertTrue(exception.getMessage().contains(paymentId.toString()));

        verify(paymentRepository).findById(paymentId);
    }
}

