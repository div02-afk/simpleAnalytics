package com.simpleAnalytics.TenetService.exception;

import java.util.UUID;

public class ApplicationNotFoundException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Application not found with ID: ";

    public ApplicationNotFoundException(String message) {
        super(message);
    }

    public ApplicationNotFoundException(UUID applicationId) {
        super(STANDARD_MESSAGE + applicationId);
    }
}
