package com.simpleAnalytics.Gateway.exception;


import java.util.UUID;

public class InsufficientCreditsException extends Exception {
    private static final String standardMessage = "Insufficient Credits for Application Id: ";

    public InsufficientCreditsException(UUID applicationId) {
        super(standardMessage + applicationId);
    }
}
