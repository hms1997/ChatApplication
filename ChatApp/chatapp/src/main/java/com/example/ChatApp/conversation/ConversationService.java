package com.example.ChatApp.conversation;

import com.example.ChatApp.entities.Conversation;
import com.example.ChatApp.entities.Message;
import com.example.ChatApp.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConversationService {

    private final ConversationRepository conversationRepository;

    public Conversation findOrCreate(User sender, User receiver, Message lastMessage) {
        // Ensure user1 < user2 to prevent duplicate conversations
        User user1 = sender.getId().compareTo(receiver.getId()) < 0 ? sender : receiver;
        User user2 = sender.getId().compareTo(receiver.getId()) < 0 ? receiver : sender;

        return conversationRepository.findByUser1AndUser2(user1, user2)
                .map(existing -> {
                    existing.setLastMessage(lastMessage);
                    return conversationRepository.save(existing);
                })
                .orElseGet(() -> {
                    return conversationRepository.save(
                            Conversation.builder()
                                    .user1(user1)
                                    .user2(user2)
                                    .lastMessage(lastMessage)
                                    .build()
                    );
                });
    }

    public List<Conversation> getConversationsForUser(User user) {
        return conversationRepository.findAllByUser(user);
    }
}

