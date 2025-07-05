package com.example.ChatApp.message.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageSentAckDto {
    private String tempId;      // The temporary ID from the client
    private String permanentId; // The new, permanent ID from the database
    private LocalDateTime timestamp;
}
