package com.simpleAnalytics.TenetService.exception;

import java.util.UUID;

public class TenetNotFoundException extends RuntimeException {

    private static final String STANDARD_MESSAGE = "Tenet not found with ID: ";

    public TenetNotFoundException(String message) {
        super(message);
    }

    public TenetNotFoundException(UUID tenetId) {
        super(STANDARD_MESSAGE + tenetId);
    }
}
