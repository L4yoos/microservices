package com.example.orderservice.adapters.rest;

import com.example.orderservice.application.OrderService;
import com.example.orderservice.application.exception.OrderNotFoundException;
import com.example.orderservice.domain.Order;
import com.example.orderservice.domain.OrderStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOrder() throws Exception {
        UUID customerId = UUID.randomUUID();
        List<String> items = List.of("item1", "item2");

        Order mockOrder = new Order(customerId, items);
        mockOrder.setCreatedAt(LocalDateTime.now());
        mockOrder.setStatus(OrderStatus.PENDING);

        when(orderService.createOrder(any(), any())).thenReturn(mockOrder);

        OrderController.CreateOrderRequest request = new OrderController.CreateOrderRequest();
        request.setCustomerId(customerId);
        request.setItems(items);

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.items[0]").value("item1"));
    }

    @Test
    void shouldReturnOrder() throws Exception {
        UUID orderId = UUID.randomUUID();
        Order order = new Order(UUID.randomUUID(), List.of("itemA"));
        order.setId(orderId);
        order.setStatus(OrderStatus.PAID);
        order.setCreatedAt(LocalDateTime.now());

        when(orderService.getOrder(orderId)).thenReturn(order);

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.status").value("PAID"));
    }

    @Test
    void shouldReturn404WhenOrderNotFound() throws Exception {
        UUID orderId = UUID.randomUUID();
        when(orderService.getOrder(orderId)).thenThrow(new OrderNotFoundException("Order not found"));

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn400WhenInvalidCreateOrder() throws Exception {
        OrderController.CreateOrderRequest request = new OrderController.CreateOrderRequest();
        request.setCustomerId(null);
        request.setItems(List.of());

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
