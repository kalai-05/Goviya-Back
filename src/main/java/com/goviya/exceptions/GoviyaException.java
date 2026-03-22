package com.goviya.exceptions;

public class GoviyaException extends RuntimeException {
    private final String errorCode;

    public GoviyaException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
