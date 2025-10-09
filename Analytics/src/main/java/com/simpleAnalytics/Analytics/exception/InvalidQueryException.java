package com.simpleAnalytics.Analytics.exception;

/**
 * Exception for invalid query parameters
 */
public class InvalidQueryException extends AnalyticsException {

    public InvalidQueryException(String message) {
        super(message);
    }

    public InvalidQueryException(String message, Throwable cause) {
        super(message, cause);
    }
}
