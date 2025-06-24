package com.example.orderservice.adapters.rest;

import com.example.orderservice.application.OrderService;
import com.example.orderservice.domain.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final Logger logger = LogManager.getLogger(OrderController.class);

    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@Valid @RequestBody CreateOrderRequest request) {
        logger.info("[OrderService] Received request to create order: customerId={}, items={}",
                request.getCustomerId(), request.getItems());
        Order order = orderService.createOrder(request.getCustomerId(), request.getItems());
        logger.info("[OrderService] Successfully created order: orderId={}", order.getId());
        return order;
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable("id") UUID id) {
        logger.info("[OrderService] Received request to get order: orderId={}", id);
        Order order = orderService.getOrder(id);
        logger.info("[OrderService] Successfully retrieved order: orderId={}", id);
        return order;
    }

    @Data
    static class CreateOrderRequest {
        @NotNull(message = "customerId is required")
        private UUID customerId;

        @NotEmpty(message = "items must not be empty")
        private List<String> items;
    }
}