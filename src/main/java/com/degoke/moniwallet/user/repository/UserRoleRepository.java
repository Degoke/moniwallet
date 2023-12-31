package com.degoke.moniwallet.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.degoke.moniwallet.user.entity.UserRole;
import com.degoke.moniwallet.user.entity.UserRoleEnum;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    Optional<UserRole> findByName(UserRoleEnum name); 
}