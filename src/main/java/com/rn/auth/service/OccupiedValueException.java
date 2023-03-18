package com.rn.auth.service;

public class OccupiedValueException extends RuntimeException {
    public OccupiedValueException(String message) {
        super(message);
    }
}
