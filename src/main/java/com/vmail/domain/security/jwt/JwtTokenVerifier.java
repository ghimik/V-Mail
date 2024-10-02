package com.vmail.domain.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

import java.security.PublicKey;

@RequiredArgsConstructor
public class JwtTokenVerifier {

    private final PublicKey publicKey;

    public Claims verify(String token) {
        return Jwts.parser()
                .verifyWith(publicKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

}
