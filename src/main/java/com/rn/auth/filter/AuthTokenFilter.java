package com.rn.auth.filter;

import com.rn.auth.service.TokenService;
import com.rn.auth.service.ClaimNotPresentException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;




    @Autowired
    public AuthTokenFilter(
        TokenService tokenService,
        UserDetailsService userDetailsService
    ) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
    }




    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (isAuthHeaderNotContainsToken(header)) {
            filterChain.doFilter(request,response);
            return;
        }

        try {
            final String token = header.substring(7);

            if(tokenService.isTokenValid(token)) {
                SecurityContext securityContext = SecurityContextHolder.getContext();

                if (securityContext.getAuthentication() == null) {
                    final String username = tokenService.extractSubject(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken contextAuthToken =
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    contextAuthToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                    securityContext.setAuthentication(contextAuthToken);
                }
            }

            filterChain.doFilter(request,response);
        } catch (
            ClaimNotPresentException |
            UsernameNotFoundException e
        ) {
            filterChain.doFilter(request,response);
        }
    }




    private boolean isAuthHeaderNotContainsToken(String header) {
        return header == null || !header.startsWith("Bearer ");
    }
}
