package com.example.paymentservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Qualifier;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Exchange orderExchange() {
        return new TopicExchange("order.exchange");
    }

    @Bean
    public Queue paymentCreatedQueue() {
        return new Queue("order.payment.created.queue");
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return new Queue("order.payment.completed.queue");
    }

    @Bean
    public Queue paymentRefundedQueue() {
        return new Queue("order.payment.refunded.queue");
    }

    @Bean
    public Queue paymentCancelledQueue() {
        return new Queue("order.payment.cancelled.queue");
    }

    @Bean
    public Binding paymentCreatedBinding(@Qualifier("paymentCreatedQueue") Queue paymentCreatedQueue, Exchange orderExchange) {
        return BindingBuilder.bind(paymentCreatedQueue)
                .to(orderExchange)
                .with("payment.created")
                .noargs();
    }

    @Bean
    public Binding paymentCompletedBinding(@Qualifier("paymentCompletedQueue") Queue paymentCompletedQueue, Exchange orderExchange) {
        return BindingBuilder.bind(paymentCompletedQueue)
                .to(orderExchange)
                .with("payment.completed")
                .noargs();
    }

    @Bean
    public Binding paymentRefundedBinding(@Qualifier("paymentRefundedQueue") Queue paymentRefundedQueue, Exchange orderExchange) {
        return BindingBuilder.bind(paymentRefundedQueue)
                .to(orderExchange)
                .with("payment.refunded")
                .noargs();
    }

    @Bean
    public Binding paymentCancelledBinding(@Qualifier("paymentCancelledQueue") Queue paymentCancelledQueue, Exchange orderExchange) {
        return BindingBuilder.bind(paymentCancelledQueue)
                .to(orderExchange)
                .with("payment.cancelled")
                .noargs();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
