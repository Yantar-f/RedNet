package com.rn.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "username_unique",
            columnNames = "username"
        ),
        @UniqueConstraint(
            name = "email_unique",
            columnNames = "email"
        )
    }
)
public class User {

    @Id
    @SequenceGenerator(
        name = "users_seq_gen",
        sequenceName = "users_seq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "users_seq_gen"
    )
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    @NotNull
    @Size(
        min = 2,
        max = 50
    )
    private String username;

    @Column(name = "email")
    @NotBlank
    @Email
    private String email;

    @Column(name = "password")
    @NotNull
    @Size(
        min = 8,
        max = 100
    )
    private String password;

    public User () {}
    public User (
        String username,
        String email,
        String password
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
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

    public void setId(Long id) {
        this.id = id;
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
