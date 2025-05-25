package com.example.registration.controller;

import com.example.registration.dto.LoginRequest;
import com.example.registration.dto.RegisterRequest;
import com.example.registration.service.AuthService;
import com.example.registration.exception.handler.GlobalExceptionHandler;
import jakarta.security.auth.message.AuthException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "注册成功"
        ));
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
            String token = authService.authenticate(request.getEmail(), request.getPassword());
            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "token", token
            ));
    }
}

