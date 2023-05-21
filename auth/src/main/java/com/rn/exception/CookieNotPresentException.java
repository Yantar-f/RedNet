package com.rn.exception;

public class CookieNotPresentException extends RuntimeException {

    public CookieNotPresentException(String cookieName) {
        super("Cookie " + cookieName + "is not present");
    }
    public CookieNotPresentException() {
        super("Cookie is not present");
    }
}