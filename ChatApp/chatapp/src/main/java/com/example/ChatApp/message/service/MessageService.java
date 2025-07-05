package com.example.ChatApp.message.service;

import com.example.ChatApp.conversation.ConversationService;
import com.example.ChatApp.entities.Message;
import com.example.ChatApp.entities.MessageStatus;
import com.example.ChatApp.entities.User;
import com.example.ChatApp.message.dtos.BulkMessageStatusUpdateDto;
import com.example.ChatApp.message.dtos.ChatMessage;
import com.example.ChatApp.message.dtos.MessageDto;
import com.example.ChatApp.message.repository.MessageRepository;
import com.example.ChatApp.message.repository.MessageStatusRepository;
import com.example.ChatApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j // Add logging
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageStatusRepository statusRepository;
    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate; // ✅ Inject the template

    @Transactional
    public Message save(ChatMessage chatMessage) {
        // ... (this method is unchanged)
        User sender = userRepository.findById(chatMessage.getSenderId())
                .orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(chatMessage.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));
        MessageStatus status = statusRepository.findByName("SENT")
                .orElseThrow(() -> new RuntimeException("Status SENT not found"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(chatMessage.getContent())
                .status(status)
                .timestamp(chatMessage.getTimestamp())
                .build();

        Message saved = messageRepository.save(message);
        conversationService.findOrCreate(sender, receiver, saved);
        return saved;
    }

    // ✅ ADD THIS NEW METHOD
    public void sendUndeliveredMessages(String userId) {
        // Find all messages for this user that are marked as SENT
        List<Message> undelivered = messageRepository.findByReceiverIdAndStatusName(userId, "SENT");

        if (!undelivered.isEmpty()) {
            log.info("Found {} undelivered messages for user {}. Pushing them now.", undelivered.size(), userId);
            undelivered.forEach(message -> {
                MessageDto dto = toDto(message);
                // Push each message to the user's private queue
                messagingTemplate.convertAndSendToUser(
                        userId,
                        "/queue/messages",
                        dto
                );
            });
        }
    }

    // ✅ MODIFY THIS METHOD
    public Page<MessageDto> getMessages(String currentUserId, String otherUserId, Pageable pageable) {
        Page<Message> messagesPage = messageRepository.findMessagesBetweenUsers(currentUserId, otherUserId, pageable);
        // Map the Page<Message> to Page<MessageDto>
        return messagesPage.map(this::toDto);
    }

    @Transactional
    public boolean processBulkStatusUpdate(BulkMessageStatusUpdateDto statusUpdate, String principalId) {
        // ... (this method is unchanged)
        MessageStatus newStatus = statusRepository.findByName(statusUpdate.getStatus())
                .orElseThrow(() -> new RuntimeException("Status not found: " + statusUpdate.getStatus()));

        List<Message> messages = messageRepository.findAllById(statusUpdate.getMessageIds());

        if (messages.isEmpty()) {
            return false;
        }

        for (Message message : messages) {
            if (!message.getReceiver().getId().equals(principalId)) {
                continue;
            }
            if (message.getStatus().getStatusOrder() < newStatus.getStatusOrder()) {
                message.setStatus(newStatus);
            }
        }

        messageRepository.saveAll(messages);
        return true;
    }

    private MessageDto toDto(Message message) {
        // ... (this method is unchanged)
        return new MessageDto(
                message.getId(),
                message.getSender().getId(),
                message.getReceiver().getId(),
                message.getContent(),
                message.getStatus().getName(),
                message.getTimestamp()
        );
    }
}
