package com.example.notificationservice.application.port;

import com.example.notificationservice.domain.Notification;

public interface NotificationRepositoryPort {
    void save(Notification notification);
    void send(Notification notification);
    boolean supportsChannel(String channel);
}
