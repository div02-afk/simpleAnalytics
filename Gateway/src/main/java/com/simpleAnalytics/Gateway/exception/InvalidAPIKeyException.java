package com.simpleAnalytics.Gateway.exception;

public class InvalidAPIKeyException extends Exception {
    private static final String standardMessage = "Invalid API Key: ";
    public InvalidAPIKeyException(String invalidApiKey) {
        super(invalidApiKey);
    }
}
