package com.rn.repository;

import com.rn.entity.EmailVerificationCredentials;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface EmailVerificationCredentialsRepository extends JpaRepository<EmailVerificationCredentials,Long> {
    @EntityGraph(value = "eager-email-verification-credentials")
    Optional<EmailVerificationCredentials> findEagerByToken(String token);

    @EntityGraph(attributePaths = {"user"})
    Optional<EmailVerificationCredentials> findWithUserByRefreshToken(String refreshToken);

    @Modifying
    @Transactional
    @Query(
        "UPDATE EmailVerificationCredentials " +
        "SET " +
            "token = :token, " +
            "refreshToken = :refreshToken, " +
            "tokenExpiration = :tokenExpiration, " +
            "refreshTokenActivation = :refreshTokenActivation, " +
            "refreshTokenExpiration = :refreshTokenExpiration " +
        "WHERE refreshToken = :byRefreshToken")
    void updateCredentialsByRefreshToken(
        @Param("byRefreshToken")String byRefreshToken,
        @Param("token") String token,
        @Param("refreshToken") String refreshToken,
        @Param("tokenExpiration") Date tokenExpiration,
        @Param("refreshTokenActivation") Date refreshTokenActivation,
        @Param("refreshTokenExpiration") Date refreshTokenExpiration
    );
}
