package com.example.paymentservice.application.port;

import com.example.paymentservice.domain.PaymentCancelledEvent;
import com.example.paymentservice.domain.PaymentCompletedEvent;
import com.example.paymentservice.domain.PaymentCreatedEvent;
import com.example.paymentservice.domain.PaymentRefundedEvent;

public interface PaymentEventPublisherPort {
    void publish(PaymentCreatedEvent event);
    void publish(PaymentCompletedEvent event);
    void publish(PaymentRefundedEvent event);
    void publish(PaymentCancelledEvent event);
}
