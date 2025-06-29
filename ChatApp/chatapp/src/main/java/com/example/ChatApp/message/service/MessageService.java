package com.example.ChatApp.message.service;

//import com.example.ChatApp.auth.entities.User;
import com.example.ChatApp.entities.Message;
import com.example.ChatApp.entities.MessageStatus;
import com.example.ChatApp.entities.User;
import com.example.ChatApp.message.dtos.ChatMessage;
import com.example.ChatApp.message.repository.MessageRepository;
import com.example.ChatApp.message.repository.MessageStatusRepository;
import com.example.ChatApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final MessageStatusRepository statusRepository;

    public Message save(ChatMessage chatMessage) {
        User sender = userRepository.findById(chatMessage.getSenderId()).orElseThrow();
        User receiver = userRepository.findById(chatMessage.getReceiverId()).orElseThrow();
        MessageStatus status = statusRepository.findByName("SENT").orElseThrow();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(chatMessage.getContent());
        message.setTimestamp(chatMessage.getTimestamp());
        message.setStatus(status);

        return messageRepository.save(message);
    }
}

