package com.rn.service;

import com.rn.entity.User;
import com.rn.exception.ClaimNotPresentException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class SimpleAuthTokenService implements AuthTokenService {

    private final String secretKey;
    private final Integer accessTokenExpirationMs;
    private final Integer refreshTokenExpirationMs;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;




    public SimpleAuthTokenService(
        @Value("${RedNet.app.jwt.secretKey}") String secretKey,
        @Value("${RedNet.app.accessTokenExpirationMs}") Integer accessTokenExpirationMs,
        @Value("${RedNet.app.refreshTokenExpirationMs}") Integer refreshTokenExpirationMs
    ) {
        this.secretKey = secretKey;
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }




    @Override
    public String generateAccessToken(User user) {
        HashMap<String,Object> rolesClaims = new HashMap<>();
        rolesClaims.put(
            "roles",
            user.getRoles().stream().map(role -> role.getDesignation().name()).toList()
        );
        return generateAccessToken(rolesClaims,user);
    }

    @Override
    public String generateAccessToken(
        Map<String, Object> extraClaims,
        User user
    ) {
        return getInitialBuilder()
            .setClaims(extraClaims)
            .setSubject(user.getId().toString())
            .setExpiration(new Date(System.currentTimeMillis() + getAccessTokenExpirationMs()))
            .compact();
    }

    @Override
    public String generateRefreshToken(User user) {
        return generateRefreshToken(new HashMap<>(),user);
    }

    @Override
    public String generateRefreshToken(Map<String, Object> extraClaims, User user) {
        return getInitialBuilder()
                .setClaims(extraClaims)
                .setSubject(user.getId().toString())
                .setExpiration(new Date(System.currentTimeMillis() + getRefreshTokenExpirationMs()))
                .compact();
    }

    @Override
    public String extractSubject(String token) throws ClaimNotPresentException {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public List<String> extractRoles(String token) {
        try {
            return (List<String>) extractClaim(token, claims -> claims.get("roles"));
        } catch (ClassCastException ex) {
            throw new ClaimNotPresentException();
        }

    }




    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
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
        return getJwtParser()
            .parseClaimsJws(token)
            .getBody();
    }

    @Override
    public boolean isTokenValid(String token) {
        try {
            getJwtParser().parseClaimsJws(token);
            return true;
        } catch (
            UnsupportedJwtException |
            MalformedJwtException |
            SecurityException |
            ExpiredJwtException |
            IllegalArgumentException e
        ) {
            return false;
        }
    }



    private JwtBuilder getInitialBuilder(){
        return Jwts
                .builder()
                .signWith(getSigningKey(), getSignatureAlgorithm())
                .setIssuedAt(new Date(System.currentTimeMillis()));
    }

    private JwtParser getJwtParser(){
        return Jwts
            .parserBuilder()
            .setSigningKey(getSigningKey())
            .build();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(
            Decoders.BASE64.decode(getSecretKey())
        );
    }

    private String getSecretKey() {
        return secretKey;
    }

    public Integer getAccessTokenExpirationMs() {
        return accessTokenExpirationMs;
    }

    public Integer getRefreshTokenExpirationMs() {
        return refreshTokenExpirationMs;
    }

    private SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }
}