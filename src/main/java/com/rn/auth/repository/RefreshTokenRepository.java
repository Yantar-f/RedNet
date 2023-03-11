package com.rn.auth.repository;

import com.rn.auth.entity.RefreshToken;
import com.rn.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByUser(User user);
    Optional<RefreshToken> findByUser_Username(String username);
}
