package com.example.ChatApp.auth.service;

import com.example.ChatApp.auth.dto.LoginRequest;
import com.example.ChatApp.auth.dto.LoginResponse;
import com.example.ChatApp.auth.utils.JwtUtil;
import com.example.ChatApp.entities.User;
import com.example.ChatApp.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByMobileNumber(request.getMobileNumber())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setMobileNumber(request.getMobileNumber());
                    newUser.setDisplayName(request.getDisplayName());
                    return userRepository.save(newUser);
                });

        String token = jwtUtil.generateToken(user.getId());
        System.out.println("✅ JWT Token issued: " + token);

        return new LoginResponse(token, user.getId(), user.getMobileNumber());
    }
}

