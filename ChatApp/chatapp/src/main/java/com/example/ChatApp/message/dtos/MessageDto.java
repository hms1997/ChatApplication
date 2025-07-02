package com.example.ChatApp.message.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private String id;
    private String senderId;
    private String receiverId;
    private String content;
    private String status; // SENT / DELIVERED / READ
    private LocalDateTime timestamp;
}
