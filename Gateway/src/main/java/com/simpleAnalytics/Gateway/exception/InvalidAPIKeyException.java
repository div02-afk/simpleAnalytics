package com.simpleAnalytics.Gateway.exception;

public class InvalidAPIKeyException extends Exception {
    public InvalidAPIKeyException(String invalidApiKey) {
        super(invalidApiKey);
    }
}
