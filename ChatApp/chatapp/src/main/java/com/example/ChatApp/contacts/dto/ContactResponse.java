package com.example.ChatApp.contacts.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactResponse {
    private String userId;
    private String mobileNumber;
    private String displayName;
}