package com.yourcode.mirae.auth.Exception;

import lombok.Getter;

@Getter
public class AuthException extends RuntimeException {
    private final String error;
    private final String message;

    public AuthException(String error, String message) {
        super(message);
        this.error = error;
        this.message = message;
    }
}