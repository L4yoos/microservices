package com.example.notificationservice.adapters.persistence;

import com.example.notificationservice.application.port.NotificationRepositoryPort;
import com.example.notificationservice.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaNotificationRepositoryAdapter implements NotificationRepositoryPort {
    private final JpaNotificationRepository jpaNotificationRepository;

    @Override
    public void save(Notification notification) {
        jpaNotificationRepository.save(notification);
    }
}
