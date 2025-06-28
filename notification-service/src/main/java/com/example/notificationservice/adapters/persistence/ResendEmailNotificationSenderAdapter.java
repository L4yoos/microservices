package com.example.notificationservice.adapters.persistence;

import com.example.notificationservice.application.port.NotificationRepositoryPort;
import com.example.notificationservice.client.ResendClient;
import com.example.notificationservice.domain.Notification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResendEmailNotificationSenderAdapter implements NotificationRepositoryPort {

    private final ResendClient resendClient;
    private final JpaNotificationRepository jpaNotificationRepository;

    @Override
    public void send(Notification notification) {
        resendClient.sendEmail(notification.getRecipient(), "Powiadomienie", notification.getMessage());
    }

    @Override
    public boolean supportsChannel(String channel) {
        return "EMAIL".equalsIgnoreCase(channel);
    }

    @Override
    public void save(Notification notification) {
        jpaNotificationRepository.save(notification);
    }
}
