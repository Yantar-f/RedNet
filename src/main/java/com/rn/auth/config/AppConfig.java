package com.rn.auth.config;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {


    @Bean
    public SignatureAlgorithm authJwtSignatureAlgorithm(){
        return SignatureAlgorithm.HS256;
    }

    @Bean
    public String authJwtSecretKey() {
        return "67556B58703273357638792F423F4428472B4B6250655368566D597133743677";
    }

    @Bean
    public Integer authJwtExpirationMs() {
        return 1800000;
    }
}
