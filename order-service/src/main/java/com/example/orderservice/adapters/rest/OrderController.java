package com.example.orderservice.adapters.rest;

import com.example.orderservice.application.OrderService;
import com.example.orderservice.domain.Order;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Order createOrder(@Valid @RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request.getCustomerId(), request.getItems());
    }

    @GetMapping("/{id}")
    public Order getOrder(@PathVariable("id") UUID id) {
        return orderService.getOrder(id);
    }

    @Data
    static class CreateOrderRequest {
        @NotNull(message = "customerId is required")
        private UUID customerId;

        @NotEmpty(message = "items must not be empty")
        private List<String> items;
    }
}