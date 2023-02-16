package com.rn.auth.repository;

import com.rn.auth.model.entity.EnumRole;
import com.rn.auth.model.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByDesignation(EnumRole designation);

    Boolean existsByDesignation(EnumRole designation);

}
