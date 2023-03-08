package com.rn.auth.service;

public class OccupiedEmailException extends OccupiedValueException {
    public OccupiedEmailException(String email) {
        super("Email " + email + " is occupied");
    }

    @Override
    public String getMessage(){
        return super.getMessage();
    }
}
