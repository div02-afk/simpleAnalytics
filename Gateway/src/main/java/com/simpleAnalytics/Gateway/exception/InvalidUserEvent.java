package com.simpleAnalytics.Gateway.exception;

public class InvalidUserEvent extends Exception {
    private static final String standardMessage = "Invalid User Event: ";

    public InvalidUserEvent(String message) {
        super(standardMessage + message);
    }
}
