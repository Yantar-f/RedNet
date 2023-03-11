package com.rn.auth.entity;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table (
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint (
            name = "email_unique",
            columnNames = "email")
    })
public class User /*extends StatisticableEntity*/ {

    @Id
    @Column (name = "username")
    private String username;

    @Column(
        name = "password",
        nullable = false,
        length = 80)
    private String password;

    @Column(
        name = "email",
        nullable = false)
    private String email;

    @ManyToMany (
        fetch = FetchType.EAGER,
        cascade = CascadeType.MERGE)
    @JoinTable (
        name = "users_to_roles",
        joinColumns = @JoinColumn (name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
    Set<Role> roles = new HashSet<>();




    public User() {}
    public User (String username) {
        this.username = username;
    }
    public User(
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

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Set<Role> getRoles(){
        return  roles;
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

    public void setRoles(Set<Role> roles){
        this.roles = roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }
}
