package com.rn.auth.repository;

import com.rn.auth.entity.EnumRole;
import com.rn.auth.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    boolean existsByDesignation(EnumRole designation);
}
