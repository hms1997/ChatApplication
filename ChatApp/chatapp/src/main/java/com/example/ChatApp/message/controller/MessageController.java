package com.example.ChatApp.message.controller;

import com.example.ChatApp.message.dtos.ChatMessage;
import com.example.ChatApp.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

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
}
