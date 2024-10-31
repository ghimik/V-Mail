package com.vmail.domain.service;


import com.vmail.domain.dto.response.AuthenticationResponse;
import com.vmail.domain.dto.request.AuthorizationRequestDto;
import com.vmail.domain.dto.request.SignUpRequestDto;
import com.vmail.domain.dto.request.TokenRequest;
import com.vmail.domain.dto.response.AuthorizationResponse;
import com.vmail.domain.model.User;
import io.jsonwebtoken.Claims;
import javax.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
        User user = new User();

        user.setUsername(requestDto.getUsername());
        user.setEmail(requestDto.getEmail());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setRole(User.ROLE_USER);

        userService.saveUser(user);
        return new AuthorizationResponse(jwtService.generateToken(user));

    }

    public AuthenticationResponse refresh(TokenRequest tokenRequest) {
        Claims claims = jwtService.validateToken(tokenRequest.getOldToken());
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
