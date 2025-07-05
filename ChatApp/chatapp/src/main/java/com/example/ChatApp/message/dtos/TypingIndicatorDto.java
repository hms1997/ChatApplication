package com.example.ChatApp.message.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypingIndicatorDto {
    private String senderId;
    private String receiverId;
    private String senderName; // ✅ Add the sender's name
    // ✅ FIX: Tell Jackson the exact JSON property name to expect.
    @JsonProperty("isTyping")
    private boolean isTyping;
}