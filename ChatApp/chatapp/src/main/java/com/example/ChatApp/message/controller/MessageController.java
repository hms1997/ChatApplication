package com.example.ChatApp.message.controller;

import com.example.ChatApp.entities.User;
import com.example.ChatApp.message.dtos.ChatMessage;
import com.example.ChatApp.message.dtos.MessageDto;
import com.example.ChatApp.message.service.MessageService;
import com.example.ChatApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserRepository userRepository;

    // This handles messages from /app/chat.sendMessage
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) {
        System.out.println("📩 Received message: " + message);
        messageService.save(message);
        // Forward to receiver’s WebSocket channel
        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(),
                "/queue/messages",
                message
        );
    }

    @GetMapping
    public ResponseEntity<List<MessageDto>> getMessages(
            @RequestParam("userId") String otherUserId,
            Principal principal) {

        String mobile = principal.getName();
        User currentUser = userRepository.findByMobileNumber(mobile)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(
                messageService.getMessages(currentUser.getId(), otherUserId)
        );
    }
}
