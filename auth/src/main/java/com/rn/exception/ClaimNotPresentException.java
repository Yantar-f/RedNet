package com.rn.exception;

public class ClaimNotPresentException extends RuntimeException {

    public ClaimNotPresentException(){
        super("");
    }

    public ClaimNotPresentException(String message){
        super(message);
    }
}