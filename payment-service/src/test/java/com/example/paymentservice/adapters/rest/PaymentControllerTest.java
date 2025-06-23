package com.example.paymentservice.adapters.rest;

import com.example.paymentservice.application.PaymentService;
import com.example.paymentservice.domain.Payment;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@AutoConfigureMockMvc(addFilters = false)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    void shouldCreatePayment() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        PaymentController.CreatePaymentRequest request = new PaymentController.CreatePaymentRequest();
        request.setOrderId(orderId);
        request.setCustomerId(customerId);
        request.setAmount(100.0);

        Payment mockPayment = new Payment(orderId, customerId, 100.0);

        when(paymentService.processPayment(any(), any(), anyDouble())).thenReturn(mockPayment);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(mockPayment.getId().toString()))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.amount").value(100.0));
    }

    @Test
    void shouldGetPaymentById() throws Exception {
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Payment payment = new Payment(orderId, customerId, 50.0);

        when(paymentService.getPayment(payment.getId())).thenReturn(payment);

        mockMvc.perform(get("/payments/" + payment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(payment.getId().toString()))
                .andExpect(jsonPath("$.orderId").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.amount").value(50.0));
    }

    @Test
    void shouldReturn404WhenPaymentNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(paymentService.getPayment(id)).thenThrow(new RuntimeException("Payment not found"));

        mockMvc.perform(get("/payments/" + id))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void shouldReturn400WhenInvalidUUID() throws Exception {
        mockMvc.perform(get("/payments/invalid-uuid"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenCreatePaymentMissingFields() throws Exception {
        String invalidJson = """
                {
                  "amount": 200.0
                }
                """;

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400WhenAmountIsNegative() throws Exception {
        PaymentController.CreatePaymentRequest request = new PaymentController.CreatePaymentRequest();
        request.setOrderId(UUID.randomUUID());
        request.setCustomerId(UUID.randomUUID());
        request.setAmount(-99.99);

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn500WhenServiceThrowsException() throws Exception {
        PaymentController.CreatePaymentRequest request = new PaymentController.CreatePaymentRequest();
        request.setOrderId(UUID.randomUUID());
        request.setCustomerId(UUID.randomUUID());
        request.setAmount(150.0);

        when(paymentService.processPayment(any(), any(), anyDouble()))
                .thenThrow(new RuntimeException("Internal failure"));

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }
}
