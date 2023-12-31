package com.degoke.moniwallet.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_roles")
public class UserRole {

    private @Id @GeneratedValue Long id;

    @Enumerated(EnumType.STRING)
    private UserRoleEnum name;

    public UserRole() {}

    public UserRole(UserRoleEnum name) {
        this.name = name;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserRoleEnum getName() {
        return this.name;
    }

    public void setName(UserRoleEnum name) {
        this.name = name;
    }
}
