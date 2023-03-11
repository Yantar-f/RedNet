package com.rn.auth.service;

public class OccupiedValueException extends RuntimeException {
    public OccupiedValueException(String message) {
        super(message);
    }

    @Override
    public String getMessage(){
        return super.getMessage();
    }
}
