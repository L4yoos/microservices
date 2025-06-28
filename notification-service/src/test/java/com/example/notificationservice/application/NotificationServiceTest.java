package com.example.notificationservice.application;

import com.example.notificationservice.application.exception.UnsupportedChannelException;
import com.example.notificationservice.application.port.NotificationEventPublisherPort;
import com.example.notificationservice.application.port.NotificationRepositoryPort;
import com.example.notificationservice.domain.Notification;
import com.example.notificationservice.domain.NotificationRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationEventPublisherPort publisher;
    private NotificationRepositoryPort emailRepository;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        publisher = mock(NotificationEventPublisherPort.class);
        emailRepository = mock(NotificationRepositoryPort.class);
        service = new NotificationService(publisher, List.of(emailRepository));
    }

    @Test
    void shouldSendNotificationWhenHandlerSupportsChannel() {
        NotificationRequestDto request = new NotificationRequestDto(
                "user@example.com",
                "Test message",
                "EMAIL"
        );

        when(emailRepository.supportsChannel("EMAIL")).thenReturn(true);

        service.send(request);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(publisher).publish(captor.capture());
        Notification publishedNotification = captor.getValue();

        assertEquals("user@example.com", publishedNotification.getRecipient());
        assertEquals("Test message", publishedNotification.getMessage());
        assertEquals("EMAIL", publishedNotification.getChannel());

        verify(emailRepository).send(publishedNotification);
    }

    @Test
    void shouldThrowExceptionWhenNoHandlerSupportsChannel() {
        NotificationRequestDto request = new NotificationRequestDto(
                "user@example.com",
                "Test message",
                "PUSH"
        );

        when(emailRepository.supportsChannel("PUSH")).thenReturn(false);

        UnsupportedChannelException ex = assertThrows(
                UnsupportedChannelException.class,
                () -> service.send(request)
        );

        assertEquals("No handler found for channel: PUSH", ex.getMessage());
        verify(publisher).publish(any(Notification.class));
        verify(emailRepository, never()).send(any());
    }
}
