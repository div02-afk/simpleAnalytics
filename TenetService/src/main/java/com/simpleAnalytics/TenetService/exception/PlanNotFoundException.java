package com.simpleAnalytics.TenetService.exception;

import java.util.UUID;

public class PlanNotFoundException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Plan not found with ID: ";

    public PlanNotFoundException(String message) {
        super(message);
    }

    public PlanNotFoundException(UUID planId) {
        super(STANDARD_MESSAGE + planId);
    }
}
