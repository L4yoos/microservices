//package com.example.notificationservice.application;
//
//import com.example.notificationservice.application.port.NotificationEventPublisherPort;
//import com.example.notificationservice.application.port.NotificationRepositoryPort;
//import com.example.notificationservice.domain.Notification;
//import com.example.notificationservice.domain.NotificationRequestDto;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.Mockito.*;
//
//class NotificationServiceTest {
//
//    private NotificationEventPublisherPort eventPublisher;
//    private NotificationRepositoryPort repository;
//    private NotificationService service;
//
//    @BeforeEach
//    void setUp() {
//        eventPublisher = mock(NotificationEventPublisherPort.class);
//        repository = mock(NotificationRepositoryPort.class);
//        service = new NotificationService(eventPublisher, repository);
//    }
//
//    @Test
//    void shouldPublishAndSaveNotification() {
//        NotificationRequestDto dto = new NotificationRequestDto(
//                "+48123123123",
//                "Test message",
//                "SMS"
//        );
//
//        ArgumentCaptor<Notification> notificationCaptor = ArgumentCaptor.forClass(Notification.class);
//
//        service.send(dto);
//
//        verify(eventPublisher, times(1)).publish(notificationCaptor.capture());
//        verify(repository, times(1)).save(notificationCaptor.capture());
//
//        Notification published = notificationCaptor.getAllValues().get(0);
//        Notification saved = notificationCaptor.getAllValues().get(1);
//
//        assertThat(published.getId()).isNotNull();
//        assertThat(published.getRecipient()).isEqualTo(dto.getRecipient());
//        assertThat(published.getMessage()).isEqualTo(dto.getMessage());
//        assertThat(published.getChannel()).isEqualTo(dto.getChannel());
//
//        assertThat(saved).isEqualTo(published);
//    }
//}
//
