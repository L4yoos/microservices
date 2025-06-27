package com.example.notificationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Exchange orderExchange() {
        return new TopicExchange("order.exchange");
    }

    @Bean
    public Queue paymentCompletedQueue() {
        return new Queue("notification.payment.completed.queue", true);
    }

    @Bean
    public Binding paymentCompletedBinding(Queue paymentCompletedQueue, Exchange orderExchange) {
        return BindingBuilder.bind(paymentCompletedQueue)
                .to(orderExchange)
                .with("payment.completed")
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
