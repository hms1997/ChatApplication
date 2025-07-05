package com.example.ChatApp.message.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkMessageStatusUpdateDto {
    private List<String> messageIds;
    private String status; // Will be "DELIVERED" or "READ"
    private String senderId; // The user who sent the original messages
}
