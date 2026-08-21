package com.example.credit_appsec_demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    record LoginRequest(String username, String password) {}

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        // VULNERABILITY #3: logging raw password
        log.info("Login attempt: username={}, password={}", request.username(), request.password());

        if ("test".equals(request.username()) && "test123".equals(request.password())) {
            return "fake-jwt-token-for-demo-purposes";
        }
        return "invalid credentials";
    }
}