package com.example.ChatApp.message.controller;

import com.example.ChatApp.entities.Message;
import com.example.ChatApp.entities.User;
import com.example.ChatApp.message.dtos.*;
import com.example.ChatApp.message.service.MessageService;
import com.example.ChatApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
@Slf4j
public class MessageController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UserRepository userRepository;

    // ✅METHOD FOR TYPING INDICATORS
    @MessageMapping("/chat.typing")
    public void handleTypingIndicator(@Payload TypingIndicatorDto typingIndicator, Principal principal) {
        String senderId = principal.getName();
        if (!typingIndicator.getSenderId().equals(senderId)) {
            return; // Security check failed
        }

        // ✅ Find the user to get their display name
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("User not found for typing indicator"));

        // ✅ Set the sender's name in the DTO before forwarding
        typingIndicator.setSenderName(sender.getDisplayName());

        log.info("Forwarding typing indicator: {}", typingIndicator);

        messagingTemplate.convertAndSendToUser(
                typingIndicator.getReceiverId(),
                "/queue/typing",
                typingIndicator
        );
    }

    // ✅ ADD THIS NEW ENDPOINT
    @PostMapping("/sync")
    public ResponseEntity<Void> syncMessages(Principal principal) {
        log.info("Client is ready. Syncing messages for user: {}", principal.getName());
        messageService.sendUndeliveredMessages(principal.getName());
        return ResponseEntity.ok().build();
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessage message, Principal principal) {
        message.setSenderId(principal.getName());
        log.info("📩 Received message: {}", message);
        Message savedMessage = messageService.save(message);

        MessageDto dtoToSend = new MessageDto(
                savedMessage.getId(),
                savedMessage.getSender().getId(),
                savedMessage.getReceiver().getId(),
                savedMessage.getContent(),
                savedMessage.getStatus().getName(),
                savedMessage.getTimestamp()
        );

        messagingTemplate.convertAndSendToUser(
                message.getReceiverId(), "/queue/messages", dtoToSend);

        // ✅ 2. Send an ACK back to the SENDER with the permanent ID
        MessageSentAckDto ackToSender = new MessageSentAckDto(
                message.getId(), // This is the tempId from the client
                savedMessage.getId(), // This is the new permanentId from the DB
                savedMessage.getTimestamp()
        );
        messagingTemplate.convertAndSendToUser(
                principal.getName(), "/queue/sent-ack", ackToSender);
    }

    @MessageMapping("/chat.updateStatus")
    public void updateStatus(@Payload BulkMessageStatusUpdateDto statusUpdate, Principal principal) {
        log.info("🔄 Received bulk status update: {}", statusUpdate);
        String principalId = principal.getName();

        boolean success = messageService.processBulkStatusUpdate(statusUpdate, principalId);

        if (success) {
            // Notify the original sender that the status has changed for a batch of messages.
            messagingTemplate.convertAndSendToUser(
                    statusUpdate.getSenderId(), // The client will provide the senderId
                    "/queue/status",
                    statusUpdate
            );
        }
    }

    @GetMapping
    public ResponseEntity<Page<MessageDto>> getMessages(
            @RequestParam("userId") String otherUserId,
            @RequestParam(defaultValue = "0") int page, // Default to page 0
            @RequestParam(defaultValue = "30") int size, // Default to 30 messages per page
            Principal principal) {

        String currentUserId = principal.getName();
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                messageService.getMessages(currentUserId, otherUserId, pageable)
        );
    }
}
