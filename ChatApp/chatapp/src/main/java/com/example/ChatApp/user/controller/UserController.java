package com.example.ChatApp.user.controller;

import com.example.ChatApp.websocket.PresenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final PresenceService presenceService;

    @GetMapping("/presence")
    public ResponseEntity<Set<String>> getOnlineUsers() {
        // Return the set of all currently online user IDs
        return ResponseEntity.ok(presenceService.getOnlineUsers());
    }
}
