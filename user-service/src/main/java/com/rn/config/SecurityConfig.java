package com.rn.config;

import com.rn.model.EnumRole;
import com.rn.entity.Role;
import com.rn.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class SecurityConfig {

    private final Integer passwordEncoderStrength;



    @Autowired
    public SecurityConfig(
        RoleRepository roleRepository,
        @Value("${RedNet.app.passwordEncoderStrength}") Integer passwordEncoderStrength
    ) {
        this.passwordEncoderStrength = passwordEncoderStrength;

        Arrays.stream(EnumRole.values()).forEach((role) -> {
            if (!roleRepository.existsByDesignation(role)) {
                roleRepository.save(new Role(role));
            }
        });
    }




    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(passwordEncoderStrength);
    }
}
