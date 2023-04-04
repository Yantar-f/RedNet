package com.rn.payload;

import java.util.List;

public class SignInResponseBody {

    private String username;
    private List<String> roles;




    public SignInResponseBody(
        String username,
        List<String> roles
    ){
        this.username = username;
        this.roles = roles;
    }




    public List<String> getRoles(){
        return roles;
    }

    public String getUsername() {
        return username;
    }
}
