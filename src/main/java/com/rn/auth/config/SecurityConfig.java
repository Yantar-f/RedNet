package com.rn.auth.config;

import com.rn.auth.filter.AuthTokenFilter;
import com.rn.auth.entity.EnumRole;
import com.rn.auth.entity.Role;
import com.rn.auth.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthTokenFilter authTokenFilter;
    private final AuthenticationEntryPoint authenticationEntryPoint;
    private final Integer passwordEncoderStrength;




    @Autowired
    public SecurityConfig(
        UserDetailsService userDetailsService,
        AuthTokenFilter authTokenFilter,
        AuthenticationEntryPoint authenticationEntryPoint,
        RoleRepository roleRepository,
        @Value("${RedNet.app.passwordEncoderStrength}") Integer passwordEncoderStrength
    ) {
        this.userDetailsService = userDetailsService;
        this.authTokenFilter = authTokenFilter;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.passwordEncoderStrength = passwordEncoderStrength;

        Arrays.stream(EnumRole.values()).forEach((role) -> {
            if (!roleRepository.existsByDesignation(role)) {
                roleRepository.save(new Role(role));
            }
        });
    }




    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
            .csrf().disable()
            .cors().disable()
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/test/admin").hasAuthority(EnumRole.ADMIN.name())
                .requestMatchers("/api/test/user").hasAuthority(EnumRole.USER.name())
                .anyRequest().authenticated())
            .exceptionHandling(exHandle -> exHandle
                .authenticationEntryPoint(authenticationEntryPoint))
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder());

        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(passwordEncoderStrength);
    }
}
