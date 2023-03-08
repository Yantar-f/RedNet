package com.rn.auth.service;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(String token) {
        super("Token " + token + " is expired");
    }

    @Override
    public String getMessage(){
        return super.getMessage();
    }
}