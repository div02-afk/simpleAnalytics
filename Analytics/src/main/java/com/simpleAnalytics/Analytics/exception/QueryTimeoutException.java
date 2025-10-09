package com.simpleAnalytics.Analytics.exception;

/**
 * Exception for query timeout
 */
public class QueryTimeoutException extends AnalyticsException {

    public QueryTimeoutException(String message) {
        super(message);
    }

    public QueryTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }
}
