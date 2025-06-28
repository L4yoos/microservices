package com.example.notificationservice.client;

import com.example.notificationservice.adapters.rabbitmq.RabbitMQEventListener;
import com.example.notificationservice.config.ResendConfig;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ResendClient {
    private static final Logger logger = LogManager.getLogger(ResendClient.class);

    private final RestTemplate restTemplate;
    private final ResendConfig resendConfig;

    public void sendEmail(String to, String subject, String text) {
        Map<String, Object> body = Map.of(
                "from", resendConfig.getFromEmail(),
                "to", to,
                "subject", subject,
                "text", text
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(resendConfig.getApiKey());
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity("https://api.resend.com/emails", request, String.class);
    }
}
