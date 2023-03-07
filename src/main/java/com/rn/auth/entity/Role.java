package com.rn.auth.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Column(name = "designation")
    @Enumerated(EnumType.STRING)
    private EnumRole designation;




    protected Role() {}
    public Role(EnumRole designation){
        this.designation = designation;
    }




    public EnumRole getDesignation() {
        return designation;
    }

    public void setDesignation(EnumRole designation) {
        this.designation = designation;
    }
}
