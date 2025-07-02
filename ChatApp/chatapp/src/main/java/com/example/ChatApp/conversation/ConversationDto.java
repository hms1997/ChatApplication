package com.example.ChatApp.conversation;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ConversationDto {
    private String contactId;
    private String displayName;
    private String lastMessage;
    private String status;
    private LocalDateTime timestamp;
}

