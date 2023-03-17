package com.rn.auth.repository;

import com.rn.auth.entity.RefreshToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUser_Id(Long id);
    @EntityGraph(value = "eager-refresh-token")
    Optional<RefreshToken> findEagerByUser_Id(Long id);
    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken SET token = :token WHERE id = :id")
    void updateToken(@Param("id") Long id, @Param("token") String token);
}
