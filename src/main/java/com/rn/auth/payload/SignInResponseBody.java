package com.rn.auth.payload;

import java.util.List;

public class SignInResponseBody {

    private List<String> roles;




    public SignInResponseBody(List<String> roles){
        this.roles = roles;
    }




    public List<String> getToken(){
        return roles;
    }

    public void setToken(List<String> roles){
        this.roles = roles;
    }
}
