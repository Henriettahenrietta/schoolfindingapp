package com.schoolfinder.api;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Authentication required");
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
