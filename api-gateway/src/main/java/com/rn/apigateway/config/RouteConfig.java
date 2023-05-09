package com.rn.apigateway.config;

import com.rn.apigateway.filter.SignInFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {
    private final SignInFilter signInFilter;
    public static final String USER_SERVICE_URI = "http://localhost:8080";
    public static final String USER_SERVICE_PATH = "/api/user";
    public static final String AUTH_PATH = "/api/auth";
    //public static final String CHAT_SERVICE_URI = "http://localhost:8081";
    //public static final String CHAT_SERVICE_PATH = "/api/chat";
    //public static final String SSE_SERVICE_URI = "http://localhost:8082";
    //public static final String SSE_SERVICE_PATH = "/api/sse";




    @Autowired
    public RouteConfig(SignInFilter signInFilter) {
        this.signInFilter = signInFilter;
    }




    @Bean
    public RouteLocator routing(RouteLocatorBuilder builder) {
        return builder.routes()
            .route(r -> r
                .path(AUTH_PATH + "/signup")
                .filters(f -> f.setPath(USER_SERVICE_PATH + "/create"))
                .uri(USER_SERVICE_URI))
            .route(r -> r
                .path(AUTH_PATH + "/signin")
                .filters(f -> f
                    .setPath(USER_SERVICE_PATH + "/authenticate")
                    .filter(signInFilter))
                .uri(USER_SERVICE_URI))
            .route(r -> r
                .path(AUTH_PATH + "/verify-email**")
                .filters(f -> f
                    .setPath(USER_SERVICE_PATH + "/verify-email")
                    .filter(signInFilter))
                .uri(USER_SERVICE_URI))
            .route(r -> r
                .path(AUTH_PATH + "/resend-email-verification")
                .filters(f -> f.setPath(USER_SERVICE_PATH + "/resend-email-verification"))
                .uri(USER_SERVICE_URI))
            /*.route(r -> r
                .path(CHAT_SERVICE_PATH_PATTERN)
                .uri(CHAT_SERVICE_URI))
            .route(r -> r
                .path(SSE_SERVICE_PATH_PATTERN)
                .uri(SSE_SERVICE_URI))*/
            .route(r -> r
                .path("/api/res2/get_user")
                .filters(f -> f
                    .filter(signInFilter))
                .uri("http://localhost:8082"))
            .build();
    }

}
