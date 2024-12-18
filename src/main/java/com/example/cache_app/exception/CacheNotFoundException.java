package com.example.cache_app.exception;

public class CacheNotFoundException extends RuntimeException {

    // Constructor that accepts a message
    public CacheNotFoundException(String message) {
        super(message);
    }

    // Constructor that accepts a message and cause
    public CacheNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

