package com.rn.auth.service;

public class OccupiedUsernameException extends OccupiedValueException{

    public OccupiedUsernameException(String username) {
        super("Username " + username + " is occupied");
    }

    @Override
    public String getMessage(){
        return super.getMessage();
    }
}
