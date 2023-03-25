package com.rn.exception;

public class OccupiedValueException extends RuntimeException {
    public OccupiedValueException(String message) {
        super(message);
    }
}
