package com.rn.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint (
            name = "email_unique",
            columnNames = "email")
    })
public class User /*extends StatisticableEntity*/ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column (
        name = "username",
        length = 60)
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

    @Column(name = "enabled")
    private boolean enabled = false;




    public User() {}
    public User (String username) {
        this.username = username;
    }
    public User(
        String username,
        String email,
        String password,
        Set<Role> roles
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }




    public Long getId(){
        return id;
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

    public boolean isEnabled() {
        return enabled;
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

    public void setRoles(Set<Role> roles){
        this.roles = roles;
    }

    public void addRole(Role role) {
        roles.add(role);
    }

    public void setEnabled(boolean status) {
        enabled = status;
    }
}
