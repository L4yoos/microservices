package com.example.notificationservice.application;

import com.example.notificationservice.application.exception.UnsupportedChannelException;
import com.example.notificationservice.application.port.NotificationEventPublisherPort;
import com.example.notificationservice.application.port.NotificationRepositoryPort;
import com.example.notificationservice.domain.Notification;
import com.example.notificationservice.domain.NotificationRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationEventPublisherPort publisher;
    private final List<NotificationRepositoryPort> notificationRepositoryPorts;

    public void send(NotificationRequestDto request) {
        Notification notification = Notification.builder()
                .id(UUID.randomUUID())
                .recipient(request.getRecipient())
                .message(request.getMessage())
                .channel(request.getChannel().toUpperCase())
                .build();

        publisher.publish(notification);

        NotificationRepositoryPort handler = notificationRepositoryPorts.stream()
                .filter(h -> h.supportsChannel(notification.getChannel()))
                .findFirst()
                .orElseThrow(() -> new UnsupportedChannelException("No handler found for channel: " + notification.getChannel()));

        handler.send(notification);
    }
}


