package com.rn.payload;

import java.util.List;

public class UserAuthenticationResponseBody {

    private String id;
    private List<String> roles;




    public UserAuthenticationResponseBody(
        String id,
        List<String> roles
    ){
        this.id = id;
        this.roles = roles;
    }




    public List<String> getRoles(){
        return roles;
    }

    public String getId() {
        return id;
    }
}
