package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Not needed in Spring Boot 3.x
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()  // Disable CSRF protection (for testing purposes)
            .headers().frameOptions().disable()  // Allow H2 console to be displayed in a frame
            .and()
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll())  // Permit all requests without authentication
            .formLogin().disable();  // Disable default form login
        return http.build();
    }
}
