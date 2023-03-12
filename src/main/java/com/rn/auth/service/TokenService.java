package com.rn.auth.service;

import com.rn.auth.entity.Role;
import com.rn.auth.entity.User;
import io.jsonwebtoken.Claims;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface TokenService {

    String extractSubject(String token);
    List<String> extractRoles(String token);
    Integer getAccessTokenExpirationMs();
    Integer getRefreshTokenExpirationMs();
    String generateAccessToken(User user);
    String generateAccessToken(Map<String, Object> extraClaims, User user);
    String generateRefreshToken(User user);
    String generateRefreshToken(Map<String, Object> extraClaims, User user);
    boolean isTokenValid(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    Claims extractAllClaims(String token);
}
