package com.example.ChatApp.contacts.dto;

import lombok.Data;

import java.util.List;

@Data
public class ContactSyncRequest {
    private List<String> contacts;
}
