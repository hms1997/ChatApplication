package com.example.ChatApp.contacts.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponse {
    private String userId;
    private String mobileNumber;
    private String displayName;
    private LocalDateTime lastSeen;
}