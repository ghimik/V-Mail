package com.vmail.domain.security;

import com.vmail.domain.security.filter.JwtAuthFilter;
import com.vmail.domain.security.jwt.JwtTokenGenerator;
import com.vmail.domain.security.jwt.JwtTokenVerifier;
import com.vmail.domain.service.JwtService;
import com.vmail.domain.util.KeyReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final String PRIVATE_KEY_PATH;

    private final String PUBLIC_KEY_PATH;

    {
        try {
            PRIVATE_KEY_PATH = Paths.get(Objects.requireNonNull(getClass()
                                    .getClassLoader()
                                    .getResource("keys/private_key.pem"))
                            .toURI())
                    .toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to find private key in keys directory. ", e);
        }

        try {
            PUBLIC_KEY_PATH = Paths.get(Objects.requireNonNull(getClass()
                                    .getClassLoader()
                                    .getResource("keys/public_key.pem"))
                            .toURI())
                    .toString();
        } catch (URISyntaxException e) {
            throw new IllegalStateException("Failed to find public key in keys directory. ", e);

        }
    }

    @Bean
    public PublicKey publicKey() {
        try {
            return KeyReader.getPublicKey(PUBLIC_KEY_PATH);
        } catch (Exception e) {
            System.err.println("Public key not found.");
            throw new IllegalStateException("Failed to load public key from path: " + PUBLIC_KEY_PATH, e);
        }
    }


    @Bean
    PrivateKey privateKey() {
        try {
            return KeyReader.getPrivateKey(PRIVATE_KEY_PATH);
        } catch (Exception e) {
            System.err.println("Private key not found.");
            throw new IllegalStateException("Failed to load private key from path: " + PUBLIC_KEY_PATH, e);
        }
    }

    @Bean
    public JwtTokenGenerator jwtTokenGenerator() {
        return new JwtTokenGenerator(privateKey());
    }

    @Bean
    public JwtTokenVerifier jwtTokenVerifier() {
        return new JwtTokenVerifier(publicKey());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter jwtAuthFilter() {
        return new JwtAuthFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {
        return
                http
                        .sessionManagement(session ->
                                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .cors(cors -> cors.disable())
                        .csrf(csrf -> csrf.disable())
                        .addFilterAfter(jwtAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                        .authorizeHttpRequests(authz -> authz
                                .requestMatchers("/api/auth/login").permitAll()
                                .requestMatchers("/api/auth/signup").permitAll()
                                .anyRequest().authenticated()

                        )
                        .build();
    }

}
