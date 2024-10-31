package com.vmail.domain.security;

import com.vmail.domain.security.filter.JwtAuthFilter;
import com.vmail.domain.security.jwt.JwtTokenGenerator;
import com.vmail.domain.security.jwt.JwtTokenVerifier;
import com.vmail.domain.service.JwtService;
import com.vmail.domain.util.KeyReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PublicKey publicKey() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("keys/public_key.pem")) {
            if (is == null) {
                throw new IllegalStateException("Public key not found at the specified path.");
            }
            return KeyReader.getPublicKey(is);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load public key", e);
        }
    }

    @Bean
    public PrivateKey privateKey() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("keys/private_key.pem")) {
            if (is == null) {
                throw new IllegalStateException("Private key not found at the specified path.");
            }
            return KeyReader.getPrivateKey(is);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load private key", e);
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

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .cors().disable()
            .csrf().disable()
            .addFilterAfter(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .authorizeRequests()
            .antMatchers("/api/auth/login", "/api/auth/signup").permitAll()
            .anyRequest().authenticated();
        return http.build();
    }

}
