package com.rn.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Token " + token + " is invalid");
    }
}
