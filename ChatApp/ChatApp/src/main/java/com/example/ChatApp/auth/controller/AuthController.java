package com.example.ChatApp.auth.controller;

import com.example.ChatApp.auth.dto.LoginRequest;
import com.example.ChatApp.auth.dto.LoginResponse;
import com.example.ChatApp.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request.getMobileNumber());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<LoginResponse> login() {
        LoginResponse response = new LoginResponse();
        return ResponseEntity.ok(response);
    }
}

