package com.rn.auth.model.payload;

public class SignInResponse {

    private String token;




    public SignInResponse(String token){
        this.token = token;
    }




    public String getToken(){
        return token;
    }

    public void setToken(String token){
        this.token = token;
    }
}
