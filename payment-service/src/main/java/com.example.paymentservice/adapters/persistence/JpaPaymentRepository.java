package com.example.paymentservice.adapters.persistence;

import com.example.paymentservice.domain.Payment;
import com.example.paymentservice.domain.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface JpaPaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByOrderId(UUID orderId);

    @Modifying
    @Transactional
    @Query("UPDATE Payment p SET p.status = :status WHERE p.id = :paymentId")
    void updateStatus(@Param("paymentId") UUID paymentId, @Param("status") PaymentStatus status);
}
