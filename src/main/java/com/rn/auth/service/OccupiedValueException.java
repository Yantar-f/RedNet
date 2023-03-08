package com.rn.auth.service;

public abstract class OccupiedValueException extends RuntimeException {
    public OccupiedValueException(String message) {
        super(message);
    }

    @Override
    public String getMessage(){
        return super.getMessage();
    }
}
