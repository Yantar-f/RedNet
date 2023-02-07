package com.rn.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(
    name = "app_users",
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
public class AppUser extends StatisticableEntity implements UserDetails {

    @Id
    @SequenceGenerator(
        name = "app_users_seq_gen",
        sequenceName = "app_users_seq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "app_users_seq_gen"
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

    @Column(name = "password")
    @NotNull
    @Size(
        min = 8,
        max = 100
    )
    private String password;

    @Column(name = "email")
    @NotBlank
    @Email
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "app_users_to_roles",
        joinColumns = @JoinColumn(name = "app_user_id"),
        inverseJoinColumns = @JoinColumn(name = "app_role_id")
    )
    Set<AppRole> appRoles = new HashSet<>();



    public AppUser() {}
    public AppUser(
        String username,
        String email,
        String password
    ) {
        this.username = username;
        this.email = email;
        this.password = password;
    }


    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
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
