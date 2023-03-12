package com.rn.auth.filter;

import com.rn.auth.service.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final String accessTokenCookieName;




    @Autowired
    public AuthTokenFilter(
        TokenService tokenService,
        UserDetailsService userDetailsService,
        @Value("${RedNet.app.accessTokenCookieName}") String accessTokenCookieName
    ) {
        this.tokenService = tokenService;
        this.accessTokenCookieName = accessTokenCookieName;
    }




    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();

        if (cookies == null) {
            filterChain.doFilter(request,response);
            return;
        }

        Cookie accessTokenCookie = Arrays.stream(cookies)
            .filter(cookie -> cookie.getName().equals(accessTokenCookieName))
            .findFirst().orElse(null);

        if (accessTokenCookie == null) {
            filterChain.doFilter(request,response);
            return;
        }

        final String accessToken = accessTokenCookie.getValue();

        if(tokenService.isTokenValid(accessToken)) {
            SecurityContext securityContext = SecurityContextHolder.getContext();
            if (securityContext.getAuthentication() == null) {
                final String username = tokenService.extractSubject(accessToken);
                final List<SimpleGrantedAuthority> authorities = tokenService.extractRoles(accessToken).stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

                UsernamePasswordAuthenticationToken contextAuthToken =
                    new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        authorities
                    );

                contextAuthToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                securityContext.setAuthentication(contextAuthToken);
            }
        }

        filterChain.doFilter(request,response);
    }
}
