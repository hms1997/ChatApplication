package com.example.ChatApp.message.service;

//import com.example.ChatApp.auth.entities.User;
import com.example.ChatApp.conversation.ConversationService;
import com.example.ChatApp.entities.Message;
import com.example.ChatApp.entities.MessageStatus;
import com.example.ChatApp.entities.User;
import com.example.ChatApp.message.dtos.ChatMessage;
import com.example.ChatApp.message.dtos.MessageDto;
import com.example.ChatApp.message.repository.MessageRepository;
import com.example.ChatApp.message.repository.MessageStatusRepository;
import com.example.ChatApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageStatusRepository statusRepository;
    private final ConversationService conversationService;

    public Message save(ChatMessage chatMessage) {
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

        // 🔁 Link/update conversation
        conversationService.findOrCreate(sender, receiver, saved);

        return saved;
    }

    public List<MessageDto> getMessages(String currentUserId, String otherUserId) {
        List<Message> messages = messageRepository.findMessagesBetweenUsers(currentUserId, otherUserId);
        return messages.stream().map(this::toDto).toList();
    }

    private MessageDto toDto(Message message) {
        MessageDto messageDto = new MessageDto();
        messageDto.setId(message.getId());
        messageDto.setSenderId(message.getSender().getId());
        messageDto.setReceiverId(message.getReceiver().getId());
        messageDto.setContent(message.getContent());
        messageDto.setStatus(message.getStatus().getName());
        messageDto.setTimestamp(message.getTimestamp());
        return messageDto;
    }

}

