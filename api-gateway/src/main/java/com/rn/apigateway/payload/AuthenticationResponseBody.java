package com.rn.apigateway.payload;

import java.util.List;

public class AuthenticationResponseBody {
    private Long id;
    private Iterable<String> roles;




    public AuthenticationResponseBody(Long id, Iterable<String> roles) {
        this.id = id;
        this.roles = roles;
    }




    public Long getId() {
        return id;
    }

    public Iterable<String> getRoles() {
        return roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setRoles(Iterable<String> roles) {
        this.roles = roles;
    }
}
