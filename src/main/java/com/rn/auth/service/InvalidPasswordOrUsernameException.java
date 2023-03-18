package com.rn.auth.service;

public class InvalidPasswordOrUsernameException extends RuntimeException{

    public InvalidPasswordOrUsernameException(String message) {
        super(message);
    }

    public InvalidPasswordOrUsernameException() {
        super("Bad credentials");
    }

}
