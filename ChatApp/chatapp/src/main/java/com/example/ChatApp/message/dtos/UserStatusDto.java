package com.example.ChatApp.message.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserStatusDto {
    private String userId;
    private Status status;

    public enum Status {
        ONLINE, OFFLINE
    }
}