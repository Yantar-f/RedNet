package com.rn.auth.entity;


import jakarta.annotation.Generated;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table (
    name = "refresh_tokens",
    uniqueConstraints = {
        @UniqueConstraint (
            name = "username_unique",
            columnNames = "user_id")
    })
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "token")
    private String token;

    @OneToOne (
        fetch = FetchType.EAGER,
        cascade = CascadeType.MERGE)
    @JoinColumn (
        name = "user_id",
        referencedColumnName = "username",
        nullable = false)
    private User user;




    protected RefreshToken() {}
    public RefreshToken(
        String token,
        User user
    ) {
        this.token = token;
        this.user = user;
    }




    public String getToken() {
        return token;
    }

    public User getUser() {
        return user;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
