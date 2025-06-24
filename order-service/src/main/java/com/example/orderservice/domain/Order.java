package com.example.orderservice.domain;

import com.example.orderservice.application.exception.InvalidOrderException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    private UUID customerId;

    @ElementCollection
    private List<String> items;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private LocalDateTime createdAt;

    public Order(UUID customerId, List<String> items) {
        if (customerId == null) {
            throw new InvalidOrderException("Customer ID is required");
        }
        if (items == null || items.isEmpty()) {
            throw new InvalidOrderException("Order must have at least one item");
        }
        this.id = UUID.randomUUID();
        this.customerId = customerId;
        this.items = items;
        this.status = OrderStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public void markAsPaid() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("Order is already paid");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot pay a canceled order");
        }
        this.status = OrderStatus.PAID;
    }

    public void cancel() {
        if (this.status == OrderStatus.PAID) {
            throw new IllegalStateException("Cannot cancel a paid order");
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Order is already canceled");
        }
        this.status = OrderStatus.CANCELLED;
    }

    public void markAsRefunded() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("Only paid orders can be refunded");
        }
        this.status = OrderStatus.REFUNDED;
    }
}
