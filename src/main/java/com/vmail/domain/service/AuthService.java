package com.vmail.domain.service;


import com.vmail.domain.dto.request.AuthenticationResponse;
import com.vmail.domain.dto.request.AuthorizationRequestDto;
import com.vmail.domain.dto.request.SignUpRequestDto;
import com.vmail.domain.dto.request.TokenRequest;
import com.vmail.domain.dto.response.AuthorizationResponse;
import com.vmail.domain.model.User;
import com.vmail.domain.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {


    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;


    public AuthorizationResponse authorize(AuthorizationRequestDto requestDto)
            throws AuthException {
        UserDetails user = userService.loadUserByUsername(requestDto.getUsername());
        if (user == null)
            throw new AuthException("Username not found");
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword()))
            throw new AuthException("Incorrect password");

        System.out.println("DB layer: user found!");
        return new AuthorizationResponse(jwtService.generateToken(user));
    }

    public AuthorizationResponse signup(SignUpRequestDto requestDto) {
        User user = User
                .builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .role(User.ROLE_USER)
                .build();

        userService.saveUser(user);
        return new AuthorizationResponse(jwtService.generateToken(user));

    }

    public AuthenticationResponse refresh(TokenRequest tokenRequest) {
        Claims claims = jwtService.validateToken(tokenRequest.getRefreshToken());
        String username = claims.getSubject();

        User userDetails = userService.loadUserByUsername(username);
        String newAccessToken = jwtService.generateToken(userDetails);
        String newRefreshToken = jwtService.generateRefresh(userDetails);

        return new AuthenticationResponse(
                newRefreshToken,
                newAccessToken
        );
    }


}
