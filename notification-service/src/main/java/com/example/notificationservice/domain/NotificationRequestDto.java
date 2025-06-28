package com.example.notificationservice.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;

@Getter
public class NotificationRequestDto {

    @NotBlank(message = "Recipient must not be blank")
    @Pattern(
            regexp = "^\\+?[0-9]{9,15}$|^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Recipient must be a valid phone number or email address"
    )
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
