package com.example.notificationservice.application.port;

import com.example.notificationservice.domain.Notification;

public interface NotificationEventPublisherPort {
    void publish(Notification notification);
}
