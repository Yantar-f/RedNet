package com.rn.auth.exception;

public class OccupiedValueException extends RuntimeException {
    public OccupiedValueException(String message) {
        super(message);
    }
}
