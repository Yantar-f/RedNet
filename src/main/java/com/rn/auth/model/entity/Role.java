package com.rn.auth.model.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "roles",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "designation_unique",
            columnNames = "designation"
        )
    }
)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "designation")
    @Enumerated(EnumType.STRING)
    private EnumRole designation;




    public Role() {}
    public Role(EnumRole designation){
        this.designation = designation;
    }




    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnumRole getDesignation() {
        return designation;
    }

    public void setDesignation(EnumRole designation) {
        this.designation = designation;
    }
}
