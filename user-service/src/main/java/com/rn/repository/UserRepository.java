package com.rn.repository;

import com.rn.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findEagerByUsernameAndEnabled(String username, boolean status);

    @EntityGraph(attributePaths = {"roles"})
    Optional<User> findEagerById(Long id);

    boolean existsByUsernameOrEmail(String username, String email);

    @Modifying
    @Transactional
    @Query("UPDATE User SET enabled = true WHERE id = :id")
    void enableUserById(@Param("id") Long id);
}