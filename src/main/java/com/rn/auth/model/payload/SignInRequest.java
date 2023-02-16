package com.rn.auth.model.payload;

public class SignInRequest {

    private String username;
    private String password;


    public SignInRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }


    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setName(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
