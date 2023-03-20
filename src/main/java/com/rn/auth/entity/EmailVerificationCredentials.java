package com.rn.auth.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedSubgraph;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.Date;

@Entity
@Table(
    name = "email_verification_credentials",
    uniqueConstraints = {
        @UniqueConstraint (
            name = "user_unique",
            columnNames = "user_id")
    })
@NamedEntityGraph (
    name = "eager-email-verification-credentials",
    attributeNodes = @NamedAttributeNode (value = "user", subgraph = "eager-user"),
    subgraphs = {
        @NamedSubgraph (
            name = "eager-user",
            attributeNodes = @NamedAttributeNode(value = "roles"))
    })
public class EmailVerificationCredentials {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (
        name = "token",
        length = 40,
        nullable = false)
    private String token;

    @Column (
        name = "refresh_token",
        length = 40,
        nullable = false)
    private String refreshToken;

    @Column(
        name = "token_expiration",
        nullable = false)
    private Date tokenExpiration;

    @Column(
        name = "refresh_token_activation",
        nullable = false)
    private Date refreshTokenActivation;

    @Column(
        name = "refresh_token_expiration",
        nullable = false)
    private Date refreshTokenExpiration;

    @OneToOne (
        fetch = FetchType.LAZY,
        cascade = CascadeType.MERGE)
    @JoinColumn (
        name = "user_id",
        referencedColumnName = "id",
        nullable = false)
    private User user;




    protected EmailVerificationCredentials(){}
    public EmailVerificationCredentials(
        String token,
        String refreshToken,
        Date tokenExpiration,
        Date refreshTokenActivation,
        Date refreshTokenExpiration,
        User user
    ) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.tokenExpiration = tokenExpiration;
        this.refreshTokenActivation = refreshTokenActivation;
        this.refreshTokenExpiration = refreshTokenExpiration;
        this.user = user;
    }




    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Date getTokenExpiration() {
        return tokenExpiration;
    }

    public Date getRefreshTokenActivation() {
        return refreshTokenActivation;
    }

    public Date getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setTokenExpiration(Date tokenExpiration) {
        this.tokenExpiration = tokenExpiration;
    }

    public void setRefreshTokenActivation(Date refreshTokenActivation) {
        this.refreshTokenActivation = refreshTokenActivation;
    }

    public void setRefreshTokenExpiration(Date refreshTokenExpiration) {
        this.refreshTokenExpiration = refreshTokenExpiration;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
