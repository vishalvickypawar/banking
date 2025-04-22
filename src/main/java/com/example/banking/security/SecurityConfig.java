package com.example.banking.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/accounts/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2Login(Customizer.withDefaults())
                .oauth2ResourceServer(oauth2->oauth2.opaqueToken(Customizer.withDefaults()));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OpaqueTokenIntrospector introspector() {
        return accessToken -> {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            try {
                ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                        "https://api.github.com/user",
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<Map<String, Object>>() {}
                );

                Map<String, Object> userInfo = response.getBody();
                if (userInfo == null || userInfo.isEmpty()) {
                    throw new OAuth2AuthenticationException("Token validation failed: Empty user info");
                }

                List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

                return new DefaultOAuth2AuthenticatedPrincipal(userInfo, authorities);
            } catch (HttpClientErrorException e) {
                throw new OAuth2AuthenticationException(
                        "Token validation failed: Unauthorized request to GitHub API - " + e.getStatusCode()
                );
            } catch (Exception e) {
                throw new OAuth2AuthenticationException("Token validation failed: " + e.getMessage());
            }
        };
    }
}