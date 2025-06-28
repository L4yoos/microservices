package com.example.notificationservice.client;

import com.example.notificationservice.config.TwilioConfig;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TwilioClient {

    private final TwilioConfig twilioConfig;

    @PostConstruct
    public void init() {
        Twilio.init(
            twilioConfig.getAccountSid(),
            twilioConfig.getAuthToken()
        );
    }

    public void sendSms(String message) {
        Message.creator(
                new PhoneNumber(twilioConfig.getRecipient()),
                new PhoneNumber(twilioConfig.getFromNumber()),
                message
        ).create();
    }
}
