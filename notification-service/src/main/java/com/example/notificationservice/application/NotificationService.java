package com.example.notificationservice.application;

import com.example.notificationservice.application.port.NotificationEventPublisherPort;
import com.example.notificationservice.application.port.NotificationRepositoryPort;
import com.example.notificationservice.domain.Notification;
import com.example.notificationservice.domain.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationEventPublisherPort eventPublisher;
    private final NotificationRepositoryPort repository;

    public void send(NotificationRequestDto request) {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .recipient(request.getRecipient())
                .message(request.getMessage())
                .channel(request.getChannel())
                .build();

        eventPublisher.publish(notification);
        repository.save(notification);
    }
}

