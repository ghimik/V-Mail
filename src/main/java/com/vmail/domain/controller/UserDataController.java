package com.vmail.domain.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/user")
public class UserDataController {

    @GetMapping("me")
    public ResponseEntity<String> me() {
        return
                ResponseEntity
                        .ok(SecurityContextHolder.getContext().getAuthentication().getName());
    }

}
