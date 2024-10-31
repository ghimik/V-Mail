package com.vmail.domain.service;


import com.vmail.domain.security.jwt.JwtTokenGenerator;
import com.vmail.domain.security.jwt.JwtTokenVerifier;
import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.nio.file.Paths;
import java.util.Objects;

@Service
public class JwtService {


    @Autowired
    @Lazy
    private JwtTokenGenerator generator;

    @Autowired
    @Lazy
    private JwtTokenVerifier verifier;


    public String generateToken(UserDetails user) {
        String token = generator.generate(user);
        System.out.println("Token generated: " + token);
        return token;
    }

    public Claims validateToken(String token) {
        return verifier.verify(token);
    }

    public String generateRefresh(UserDetails user) {
        String refreshToken = generator.generateRefresh(user);
        System.out.println("Refresh Token generated: " + refreshToken);
        return refreshToken;
    }


}
