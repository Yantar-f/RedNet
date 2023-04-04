package com.rn.service;

import com.rn.entity.User;
import io.jsonwebtoken.Claims;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface AuthTokenService {

    String extractSubject(String token);
    List<String> extractRoles(String token);
    Date extractExpiration(String token);
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
