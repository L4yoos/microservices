package com.example.paymentservice.application.port;

import com.example.paymentservice.domain.PaymentCompletedEvent;

public interface PaymentEventPublisherPort {
    void publish(PaymentCompletedEvent event);
}
