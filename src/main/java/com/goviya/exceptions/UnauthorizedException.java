package com.goviya.exceptions;

public class UnauthorizedException extends GoviyaException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }
}
