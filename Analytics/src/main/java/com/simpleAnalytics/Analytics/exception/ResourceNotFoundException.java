package com.simpleAnalytics.Analytics.exception;

/**
 * Exception when resource is not found
 */
public class ResourceNotFoundException extends AnalyticsException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
