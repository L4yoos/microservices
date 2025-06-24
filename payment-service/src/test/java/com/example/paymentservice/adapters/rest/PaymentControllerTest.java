package com.example.paymentservice.adapters.rest;

import com.example.paymentservice.application.PaymentService;
import com.example.paymentservice.application.exception.PaymentNotFoundException;
import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.PaymentStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private UUID paymentId;
    private UUID orderId;
    private UUID customerId;

    private Payment samplePayment;

    @BeforeEach
    void setUp() {
        paymentId = UUID.randomUUID();
        orderId = UUID.randomUUID();
        customerId = UUID.randomUUID();

        samplePayment = Payment.builder()
                .id(paymentId)
                .orderId(orderId)
                .customerId(customerId)
                .amount(100.0)
                .status(PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void createPayment_ValidRequest_ReturnsCreated() throws Exception {
        PaymentController.CreatePaymentRequest request = new PaymentController.CreatePaymentRequest();
        request.setOrderId(orderId);
        request.setCustomerId(customerId);
        request.setAmount(150.0);

        Mockito.when(paymentService.processPayment(orderId, customerId, 150.0))
                .thenReturn(samplePayment);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(paymentId.toString()))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.amount").value(100.0)); // zwracamy samplePayment.amount
    }

    @Test
    void createPayment_InvalidRequest_MissingFields_ReturnsBadRequest() throws Exception {
        // brak customerId i orderId + amount ujemne
        String invalidJson = """
            {
                "amount": -10
            }
            """;

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void completePayment_ValidId_ReturnsOk() throws Exception {
        Mockito.when(paymentService.completePayment(paymentId)).thenReturn(samplePayment);

        mockMvc.perform(post("/payments/{id}/complete", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId.toString()));
    }

    @Test
    void refundPayment_ValidId_ReturnsOk() throws Exception {
        Mockito.when(paymentService.refundPayment(paymentId)).thenReturn(samplePayment);

        mockMvc.perform(post("/payments/{id}/refund", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId.toString()));
    }

    @Test
    void cancelPayment_ValidId_ReturnsOk() throws Exception {
        Mockito.when(paymentService.cancelPayment(paymentId)).thenReturn(samplePayment);

        mockMvc.perform(post("/payments/{id}/cancel", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId.toString()));
    }

    @Test
    void getPayment_ValidId_ReturnsOk() throws Exception {
        Mockito.when(paymentService.getPaymentOrThrow(paymentId)).thenReturn(samplePayment);

        mockMvc.perform(get("/payments/{id}", paymentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(paymentId.toString()));
    }

    @Test
    void completePayment_InvalidId_ReturnsNotFound() throws Exception {
        Mockito.when(paymentService.completePayment(paymentId))
                .thenThrow(new PaymentNotFoundException("Payment not found"));

        mockMvc.perform(post("/payments/{id}/complete", paymentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void refundPayment_InvalidId_ReturnsNotFound() throws Exception {
        Mockito.when(paymentService.refundPayment(paymentId))
                .thenThrow(new PaymentNotFoundException("Payment not found"));

        mockMvc.perform(post("/payments/{id}/refund", paymentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelPayment_InvalidId_ReturnsNotFound() throws Exception {
        Mockito.when(paymentService.cancelPayment(paymentId))
                .thenThrow(new PaymentNotFoundException("Payment not found"));

        mockMvc.perform(post("/payments/{id}/cancel", paymentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPayment_InvalidId_ReturnsNotFound() throws Exception {
        Mockito.when(paymentService.getPaymentOrThrow(paymentId))
                .thenThrow(new PaymentNotFoundException("Payment not found"));

        mockMvc.perform(get("/payments/{id}", paymentId))
                .andExpect(status().isNotFound());
    }

}
