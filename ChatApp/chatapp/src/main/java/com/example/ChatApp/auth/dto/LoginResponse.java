package com.example.ChatApp.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Generates getters, setters, equals, hashCode, and toString
@NoArgsConstructor // Generates no-arg constructor
@AllArgsConstructor // Generates constructor with all fields
public class LoginResponse {
    private String token;
    private String userId;
    private String mobileNumber;
}

