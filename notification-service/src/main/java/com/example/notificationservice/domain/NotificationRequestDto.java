package com.example.notificationservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class NotificationRequestDto {

    @NotBlank(message = "Recipient must not be blank")
    @Pattern(regexp = "^\\+48\\d{9}$", message = "Recipient must be a valid Polish phone number starting with +48")
    private final String recipient;

    @NotBlank(message = "Message must not be blank")
    private final String message;

    @NotBlank(message = "Channel must not be blank")
    private final String channel;


    @JsonCreator
    public NotificationRequestDto(
            @JsonProperty("recipient") String recipient,
            @JsonProperty("message") String message,
            @JsonProperty("channel") String channel
    ) {
        this.recipient = recipient;
        this.message = message;
        this.channel = channel;
    }
}
