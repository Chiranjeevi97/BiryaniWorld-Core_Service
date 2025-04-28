package com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.config;

import com.biryaniworldtech.BiryaniWorld.BiryaniWorld_core.security.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider();
    }
} 