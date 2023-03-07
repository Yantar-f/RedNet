package com.rn.auth.payload;

import jakarta.validation.constraints.NotBlank;

public class SignInRequestBody {

    @NotBlank(message = "Username shouldn`t be blank")
    private String username;

    @NotBlank(message = "Password shouldn`t be Blank")
    private String password;




    public SignInRequestBody(
        String username,
        String password
    ) {
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
