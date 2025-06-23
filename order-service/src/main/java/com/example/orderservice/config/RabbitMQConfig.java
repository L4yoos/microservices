package com.example.orderservice.config;

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
    public Queue paymentCompletedQueue() {
        return new Queue("order.payment.completed.queue");
    }

    @Bean
    public Queue cancellationRequestedQueue() {
        return new Queue("order.cancellation.requested.queue");
    }

    @Bean
    public Binding paymentCompletedBinding(@Qualifier("paymentCompletedQueue") Queue paymentCompletedQueue, Exchange orderExchange) {
        return BindingBuilder.bind(paymentCompletedQueue)
                .to(orderExchange)
                .with("payment.completed")
                .noargs();
    }

    @Bean
    public Binding cancellationRequestedBinding(@Qualifier("cancellationRequestedQueue") Queue cancellationRequestedQueue, Exchange orderExchange) {
        return BindingBuilder.bind(cancellationRequestedQueue)
                .to(orderExchange)
                .with("order.cancellation.requested")
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