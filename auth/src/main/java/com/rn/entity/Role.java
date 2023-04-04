package com.rn.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roles")
public class Role {

    @Id
    @Column (name = "designation")
    @Enumerated (EnumType.STRING)
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
