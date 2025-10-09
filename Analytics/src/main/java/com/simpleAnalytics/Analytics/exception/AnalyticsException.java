package com.simpleAnalytics.Analytics.exception;

/**
 * Base exception for Analytics service
 */
public class AnalyticsException extends RuntimeException {

    public AnalyticsException(String message) {
        super(message);
    }

    public AnalyticsException(String message, Throwable cause) {
        super(message, cause);
    }
}
