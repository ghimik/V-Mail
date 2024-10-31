package com.vmail.domain.security.filter;

import com.vmail.domain.dto.response.AuthorizationResponse;
import com.vmail.domain.security.jwt.JwtAuthentication;
import com.vmail.domain.service.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    private Optional<String> getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith(AuthorizationResponse.BEARER)) {
            return Optional.of(bearer.substring(AuthorizationResponse.BEARER.length()));
        }
        return Optional.empty();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final Optional<String> token = getTokenFromRequest(request);
        if (token.isEmpty()) {
            System.out.println("Token is not present");
        } else {
            System.out.println("Token present: " + token.get());
            Claims claims;
            try {
                claims = jwtService.validateToken(token.get());
            } catch (Exception e) {
                System.out.println("Token is not verified");
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            System.out.println("Token verified");
            Authentication authentication = JwtAuthentication.fromClaims(claims);
            System.out.println("Authentication generated");

            authentication.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            System.out.println("Context set: " + SecurityContextHolder.getContext());

        }
        filterChain.doFilter(request, response);

    }
}
