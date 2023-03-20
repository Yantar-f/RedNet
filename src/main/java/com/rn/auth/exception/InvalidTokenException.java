package com.rn.auth.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Token " + token + " is invalid");
    }
}
