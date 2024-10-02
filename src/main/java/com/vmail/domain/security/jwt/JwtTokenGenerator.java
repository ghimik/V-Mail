package com.vmail.domain.security.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.PrivateKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;

// TODO разные ключи для токенов

@RequiredArgsConstructor
public class JwtTokenGenerator {

    private final PrivateKey privateKey;

    private final Integer EXPIRATION_TIME_MILS = 1000 * 60 * 15;

    private final Integer REFRESH_EXPIRATION_TIME_MILS = 1000 * 60 * 24 * 7;


    public String generate(UserDetails user) {
        Instant now = Clock.systemUTC().instant();

        return Jwts.builder()
                .claim("name", user.getUsername())
                .claim("authorities", user.getAuthorities())
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MILS))
                .signWith(privateKey)
                .compact();
    }

    public String generateRefresh(UserDetails user) {
        Instant now = Clock.systemUTC().instant();

        return Jwts.builder()
                .subject(user.getUsername())
                .issuedAt(Date.from(now))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME_MILS))
                .signWith(privateKey)
                .compact();
    }


}
