package com.rn.auth.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Qualifier;
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
    private final SignatureAlgorithm signatureAlgorithm;




    public AuthJwtService(
        @Qualifier("authJwtSecretKey") String secretKey,
        @Qualifier("authJwtExpirationMs") Integer tokenExpirationMs,
        @Qualifier("authJwtSignatureAlgorithm") SignatureAlgorithm signatureAlgorithm
    ) {
        this.secretKey = secretKey;
        this.tokenExpirationMs = tokenExpirationMs;
        this.signatureAlgorithm = signatureAlgorithm;
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
            .setExpiration(new Date(System.currentTimeMillis() + getTokenExpirationMs()))
            .signWith(getSigningKey(), getSignatureAlgorithm())
            .compact();
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
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) throws ClaimNotPresentException {
        final Claims claims = extractAllClaims(token);
        return Optional
            .ofNullable(claimsResolver.apply(claims))
            .orElseThrow(ClaimNotPresentException::new);
    }

    @Override
    public Claims extractAllClaims(String token) throws ClaimNotPresentException {
        return Optional
            .ofNullable(getJwtParser()
                .parseClaimsJws(token)
                .getBody())
            .orElseThrow(ClaimNotPresentException::new);
    }

    @Override
    public boolean isTokenValid(String token) {
        JwtParser jwtParser = getJwtParser();

        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (
            SecurityException |
            MalformedJwtException |
            ExpiredJwtException |
            UnsupportedJwtException |
            IllegalArgumentException e
        ) {
            return false;
        }
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

    private Integer getTokenExpirationMs() {
        return tokenExpirationMs;
    }

    private SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }
}