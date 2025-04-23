package com.example.banking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf->csrf.disable())
                .authorizeHttpRequests(authz->authz
                        .requestMatchers("/login","register")
                        .permitAll()
                        .requestMatchers("/api/accounts/**")
                        .hasAnyRole("USER","ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login->login.loginPage("/login").permitAll())
                .logout(logout->logout.logoutUrl("/logout").permitAll());
        return http.build();
    }
}
