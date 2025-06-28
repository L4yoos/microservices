//package com.example.notificationservice.adapters.rabbitmq;
//
//import com.example.notificationservice.application.exception.UnsupportedChannelException;
//import com.example.notificationservice.config.TwilioConfig;
//import com.example.notificationservice.domain.Notification;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.rest.api.v2010.account.MessageCreator;
//import com.twilio.type.PhoneNumber;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.MockedStatic;
//import org.mockito.Mockito;
//
//import java.util.UUID;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//
//class RabbitMQEventPublisherTest {
//
//    private RabbitMQEventPublisher publisher;
//    private TwilioConfig config;
//    private ObjectMapper objectMapper;
//
//    @BeforeEach
//    void setUp() {
//        config = new TwilioConfig();
//        objectMapper = new ObjectMapper();
//        publisher = new RabbitMQEventPublisher(config, objectMapper);
//
//        publisher.twilioPhone = "+48111111111";
//        publisher.twilioFromPhoneNumber = "+12222222222";
//    }
//
//    @Test
//    void shouldSendSmsWhenChannelIsSMS() {
//        Notification notification = new Notification(
//                UUID.randomUUID(),
//                "+48123123123",
//                "Test message",
//                "SMS"
//        );
//
//        try (MockedStatic<Message> mockedStatic = Mockito.mockStatic(Message.class)) {
//            MessageCreator creatorMock = Mockito.mock(MessageCreator.class);
//            Message messageMock = Mockito.mock(Message.class);
//
//            mockedStatic.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), any(String.class)))
//                    .thenReturn(creatorMock);
//
//            Mockito.when(creatorMock.create()).thenReturn(messageMock);
//
//            assertDoesNotThrow(() -> publisher.publish(notification));
//        }
//    }
//
//    @Test
//    void shouldThrowExceptionForUnsupportedChannel() {
//        Notification notification = new Notification(UUID.randomUUID(), "+48123123123", "Test message", "EMAIL");
//
//        UnsupportedChannelException exception = assertThrows(
//                UnsupportedChannelException.class,
//                () -> publisher.publish(notification)
//        );
//
//        assertEquals("Only SMS notifications are supported.", exception.getMessage());
//    }
//}
