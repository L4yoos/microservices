package com.example.notificationservice.adapters.rest;

import com.example.notificationservice.application.NotificationService;
import com.example.notificationservice.domain.NotificationRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private static final Logger logger = LogManager.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    @PostMapping
    public void sendNotification(@Valid @RequestBody NotificationRequestDto dto) {
        logger.info("[NotificationController] Received request to create notification: recipient={}, message={}", dto.getRecipient(), dto.getMessage());
        notificationService.send(dto);
        logger.info("[NotificationController] Successfully created notification");
    }
}
