package com.rn.apigateway.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class GatewayService {
    private final String accessTokenCookieName;
    private final String accessTokenCookiePath;




    public GatewayService(
        @Value ("${RedNet.app.accessTokenCookieName}") String accessTokenCookieName,
        @Value ("${RedNet.app.accessTokenCookiePath}") String accessTokenCookiePath
    ) {
        this.accessTokenCookieName = accessTokenCookieName;
        this.accessTokenCookiePath = accessTokenCookiePath;
    }



    public ResponseEntity<String> signOut(ServerHttpRequest request) {
        SecurityContextHolder.getContext().setAuthentication(null);
        return ResponseEntity.ok()
            .header(
                HttpHeaders.SET_COOKIE,
                generateCleaningCookie(accessTokenCookieName, accessTokenCookiePath).toString()
            )
            .body("Sign out: successful");
    }




    private ResponseCookie generateCleaningCookie(String name, String path) {
        return ResponseCookie.from(name)
            .path(path)
            .maxAge(0)
            .build();
    }
}
