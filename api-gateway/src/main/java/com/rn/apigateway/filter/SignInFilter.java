package com.rn.apigateway.filter;

import com.rn.apigateway.payload.AuthenticationResponseBody;
import com.rn.apigateway.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class SignInFilter implements GatewayFilter {

    private final JwtService jwtService;
    private final String accessTokenCookieName;
    private final String accessTokenCookiePath;





    @Autowired
    public SignInFilter(
        JwtService jwtService,
        @Value("${RedNet.app.accessTokenCookieName}") String accessTokenCookieName,
        @Value("${RedNet.app.accessTokenCookiePath}") String accessTokenCookiePath
    ) {
        this.jwtService = jwtService;
        this.accessTokenCookieName = accessTokenCookieName;
        this.accessTokenCookiePath = accessTokenCookiePath;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            if (response.getStatusCode().is2xxSuccessful()){
                response.addCookie(generateAccessCookie(
                    jwtService.generateAccessToken(new AuthenticationResponseBody(
                        Long.valueOf(response.getHeaders().getFirst("Auth-User-Id")),
                        response.getHeaders().get("Auth-User-Roles")
                    ))
                ));
            }
        }));
    }




    private ResponseCookie generateAccessCookie(String value) {
        return ResponseCookie.from(accessTokenCookieName, value)
            .path(accessTokenCookiePath)
            .httpOnly(true)
            .maxAge(TimeUnit.MILLISECONDS.toSeconds(jwtService.getAccessTokenExpirationMs()))
            .build();
    }
}
