package com.example.notificationservice.adapters.rabbitmq;

import com.example.notificationservice.application.exception.UnsupportedChannelException;
import com.example.notificationservice.client.ResendClient;
import com.example.notificationservice.client.TwilioClient;
import com.example.notificationservice.domain.Notification;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RabbitMQEventPublisherTest {

    private RabbitMQEventPublisher publisher;
    private TwilioClient twilioClient;
    private ResendClient resendClient;

    @BeforeEach
    void setUp() {
        twilioClient = mock(TwilioClient.class);
        resendClient = mock(ResendClient.class);
        publisher = new RabbitMQEventPublisher(twilioClient, resendClient);
    }

    @Test
    void shouldSendSmsWhenChannelIsSMS() {
        Notification notification = new Notification(
                UUID.randomUUID(),
                "+48123123123",
                "Test message",
                "SMS"
        );

        try (MockedStatic<Message> mockedStatic = Mockito.mockStatic(Message.class)) {
            MessageCreator creatorMock = Mockito.mock(MessageCreator.class);
            Message messageMock = Mockito.mock(Message.class);

            mockedStatic.when(() -> Message.creator(any(PhoneNumber.class), any(PhoneNumber.class), any(String.class)))
                    .thenReturn(creatorMock);

            Mockito.when(creatorMock.create()).thenReturn(messageMock);

            assertDoesNotThrow(() -> publisher.publish(notification));
        }
    }

    @Test
    void shouldSendEmailViaResendClient() {
        Notification notification = new Notification(
                UUID.randomUUID(),
                "user@example.com",
                "Test email message",
                "EMAIL"
        );

        publisher.publish(notification);

        verify(resendClient, times(1)).sendEmail("user@example.com", "Notification", "Test email message");
        verifyNoInteractions(twilioClient);
    }

    @Test
    void shouldThrowExceptionForUnsupportedChannel() {
        Notification notification = new Notification(UUID.randomUUID(), "+48123123123", "Test message", "PUSH");

        UnsupportedChannelException exception = assertThrows(
                UnsupportedChannelException.class,
                () -> publisher.publish(notification)
        );

        assertEquals("Unsupported notification channel: PUSH", exception.getMessage());
    }
}
