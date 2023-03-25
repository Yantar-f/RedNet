package com.rn.repository;

import com.rn.entity.EnumRole;
import com.rn.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, EnumRole> {
    boolean existsByDesignation(EnumRole designation);
}
