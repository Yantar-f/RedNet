package com.rn.auth.service;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

public interface AuthTokenService {

    String extractSubject(String token) throws ClaimNotPresentException;
    Date extractExpiration(String token) throws ClaimNotPresentException;
    String generateToken(UserDetails userDetails);
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);
    boolean isTokenValid(String token, UserDetails userDetails);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ClaimNotPresentException;
    Claims extractAllClaims(String token);
}