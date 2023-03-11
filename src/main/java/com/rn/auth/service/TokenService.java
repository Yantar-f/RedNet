package com.rn.auth.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;

public interface TokenService {

    String extractSubject(String token);
    Integer getAccessTokenExpirationMs();
    Integer getRefreshTokenExpirationMs();
    String generateAccessToken(String username);
    String generateAccessToken(Map<String, Object> extraClaims, String username);
    String generateRefreshToken(String username);
    String generateRefreshToken(Map<String, Object> extraClaims, String username);
    boolean isTokenValid(String token);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    Claims extractAllClaims(String token);
}
