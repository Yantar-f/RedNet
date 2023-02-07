package com.rn.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "app_roles")
public class AppRole {

    @Id
    @SequenceGenerator(
        name = "app_roles_seq_gen",
        sequenceName = "app_roles_seq",
        allocationSize = 1
    )
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "app_roles_seq_gen"
    )
    @Column(name = "id")
    private Long id;


    @Column(name = "designation")
    @Enumerated(EnumType.STRING)
    private EnumAppRole designation;

    public AppRole() {}
    public AppRole(
        EnumAppRole designation
    ) {
        this.designation = designation;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumAppRole getDesignation() {
        return designation;
    }

    public void setDesignation(EnumAppRole designation) {
        this.designation = designation;
    }
}
