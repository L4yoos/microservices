package com.example.notificationservice.application.exception;

public class UnsupportedChannelException extends RuntimeException {
    public UnsupportedChannelException(String message) {
        super(message);
    }
}
