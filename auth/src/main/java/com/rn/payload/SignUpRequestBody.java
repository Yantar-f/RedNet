package com.rn.payload;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignUpRequestBody {

    @NotBlank(message = "Username shouldn`t be blank")
    @Size(max = 60, message = "Username shouldn`t be more than 60 chars")
    private String username;

    @NotBlank(message = "Email shouldn`t be blank")
    @Email(message = "Email should be valid")
    private String email;

    @NotNull(message = "Password shouldn`t be null")
    @Size(min = 8, max = 80, message = "Password should be between 8 and 80 chars")
    private String password;




    public SignUpRequestBody(
        String username,
        String email,
        String password
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
    }




    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
