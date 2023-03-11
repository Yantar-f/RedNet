package com.rn.auth.service;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Token " + token + " is invalid");
    }

    @Override
    public String getMessage(){
        return super.getMessage();
    }
}
