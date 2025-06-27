package com.example.notificationservice.adapters.persistence;

import com.example.notificationservice.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface JpaNotificationRepository extends JpaRepository<Notification, UUID> {
}
