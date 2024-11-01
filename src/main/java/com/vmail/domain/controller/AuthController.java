package com.vmail.domain.controller;

import com.vmail.domain.dto.response.AuthenticationResponse;
import com.vmail.domain.dto.request.AuthorizationRequestDto;
import com.vmail.domain.dto.request.SignUpRequestDto;
import com.vmail.domain.dto.request.TokenRequest;
import com.vmail.domain.dto.response.AuthorizationResponse;
import com.vmail.domain.dto.response.SimpleResponse;
import com.vmail.domain.service.AuthService;
import com.vmail.domain.service.JwtService;
import javax.security.auth.message.AuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtService jwtService;


    @PostMapping("login")
    public ResponseEntity<SimpleResponse> login
            (@RequestBody AuthorizationRequestDto requestDto) {

        try {
            AuthorizationResponse header = authService.authorize(requestDto);
            return successAuth(header);

        } catch (AuthException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .header("WWW-Authenticate", "enjoi;)")
                    .build();
        }
    }

    @PostMapping("signup")
    public ResponseEntity<SimpleResponse> signup(@RequestBody SignUpRequestDto requestDto) {

        try {
            AuthorizationResponse header = authService.signup(requestDto);
            return successAuth(header);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("refresh")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody TokenRequest tokenRequest) {
        try {
            return ResponseEntity.ok(authService.refresh(tokenRequest));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    private ResponseEntity<SimpleResponse> successAuth(AuthorizationResponse header) {
        SimpleResponse response = new SimpleResponse("Authentication successful");
        return ResponseEntity
                .ok()
                .header(header.getHeaderName(), header.getHeaderValue())
                .body(response);
    }

}
