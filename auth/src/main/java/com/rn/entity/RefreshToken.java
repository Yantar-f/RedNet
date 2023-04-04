package com.rn.entity;


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
    name = "refresh_tokens",
    uniqueConstraints = {
        @UniqueConstraint (
            name = "user_id_unique",
            columnNames = "user_id")
    })
@NamedEntityGraph(
    name = "eager-refresh-token",
    attributeNodes = @NamedAttributeNode(value = "user", subgraph = "eager-user"),
    subgraphs = {
        @NamedSubgraph(
            name = "eager-user",
            attributeNodes = @NamedAttributeNode(value = "roles"))
    })
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "token")
    private String token;

    @Column(name = "exp_date")
    private Date expDate;

    @OneToOne (
        fetch = FetchType.LAZY,
        cascade = CascadeType.MERGE)
    @JoinColumn (
        name = "user_id",
        referencedColumnName = "id",
        nullable = false)
    private User user;




    protected RefreshToken() {}
    public RefreshToken(
        String token,
        Date expDate,
        User user
    ) {
        this.token = token;
        this.expDate = expDate;
        this.user = user;
    }




    public Long getId(){
        return id;
    }

    public String getToken() {
        return token;
    }

    public Date getExpDate() {
        return expDate;
    }

    public User getUser() {
        return user;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

}
