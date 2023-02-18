package com.rn.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class AuthJwtService implements AuthTokenService {


    private final String secretKey;
    private final Integer tokenExpirationMs;




    public AuthJwtService(
        @Value("${RedNet.app.authJwtSecretKey}") String secretKey,
        @Value("${RedNet.app.authJwtExpirationMs}") Integer tokenExpirationMs
    ) {
        this.secretKey = secretKey;
        this.tokenExpirationMs = tokenExpirationMs;
    }




    @Override
    public String extractSubject(String token) throws ClaimNotPresentException {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public Date extractExpiration(String token) throws ClaimNotPresentException {
        return extractClaim(token, Claims::getExpiration);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(),userDetails);
    }

    @Override
    public String generateToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails
    ) {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + tokenExpirationMs))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    /*
    Неправильная проверка валидоности токена (возможно), надо добавить проверку сигнатуры токена
     */
    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            final String tokenUsername = extractSubject(token);
            final String userDetailsUsername = userDetails.getUsername();

            return
                tokenUsername.equals(userDetailsUsername) && isTokenNotExpired(token);
        } catch (ClaimNotPresentException e) {
            return false;
        }
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ClaimNotPresentException {
        final Claims claims = extractAllClaims(token);
        return Optional
            .ofNullable(claimsResolver.apply(claims))
            .orElseThrow(ClaimNotPresentException::new);
    }

    @Override
    public Claims extractAllClaims(String token) {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }




    private boolean isTokenNotExpired(String token) {
        return !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date(System.currentTimeMillis()));
        } catch (ClaimNotPresentException e) {
            return false;
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(secretKey)
        );
    }

}
