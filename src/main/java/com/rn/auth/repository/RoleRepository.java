package com.rn.auth.repository;

import com.rn.auth.model.EnumRole;
import com.rn.auth.model.Role;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository {
    Optional<Role> findByDesignation(EnumRole designation);

    Boolean existsByDesignation(EnumRole designation);

}
