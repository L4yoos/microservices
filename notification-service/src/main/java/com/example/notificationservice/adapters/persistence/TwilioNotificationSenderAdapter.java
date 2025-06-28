package com.example.notificationservice.adapters.persistence;

import com.example.notificationservice.application.port.NotificationRepositoryPort;
import com.example.notificationservice.client.TwilioClient;
import com.example.notificationservice.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TwilioNotificationSenderAdapter implements NotificationRepositoryPort {

    private final TwilioClient twilioClient;
    private final JpaNotificationRepository jpaNotificationRepository;

    @Override
    public void send(Notification notification) {
        twilioClient.sendSms(notification.getMessage());
    }

    @Override
    public boolean supportsChannel(String channel) {
        return "SMS".equalsIgnoreCase(channel);
    }

    @Override
    public void save(Notification notification) {
        jpaNotificationRepository.save(notification);
    }
}

