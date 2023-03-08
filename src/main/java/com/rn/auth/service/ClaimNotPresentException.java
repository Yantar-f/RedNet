package com.rn.auth.service;

public class ClaimNotPresentException extends RuntimeException {

    public ClaimNotPresentException(){
        super("");
    }

    public ClaimNotPresentException(String message){
        super(message);
    }

    public String getMessage(){
        return super.getMessage();
    }
}
