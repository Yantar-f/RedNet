package com.rn.auth.repository;

import com.rn.auth.entity.EnumRole;
import com.rn.auth.entity.Role;

import java.util.Optional;

public interface RoleRepository {
    Optional<Role> findByDesignation(EnumRole designation);

    Boolean existsByDesignation(EnumRole designation);

}
