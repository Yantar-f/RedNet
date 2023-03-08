package com.rn.auth.payload;

public class SimpleResponseBody {

    private final String message;




    public SimpleResponseBody(String message) {
        this.message = message;
    }




    public String getMessage() {
        return message;
    }
}
