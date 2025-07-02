package com.example.ChatApp.conversation;

import com.example.ChatApp.entities.Conversation;
import com.example.ChatApp.entities.User;
import com.example.ChatApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserRepository userRepository;

    @GetMapping
    public List<ConversationDto> getConversations(Principal principal) {
        // ✅ FIX: The principal's name is now the User ID.
        String currentUserId = principal.getName();
        User currentUser = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found for ID: " + currentUserId));

        List<Conversation> conversations = conversationService.getConversationsForUser(currentUser);

        return conversations.stream().map(conversation -> {
                    // This logic remains correct
                    User contact = conversation.getUser1().getId().equals(currentUserId) ? conversation.getUser2() : conversation.getUser1();

                    // Safe check for last message
                    if (conversation.getLastMessage() == null) {
                        return null; // Or return a DTO with default values
                    }

                    return ConversationDto.builder()
                            .contactId(contact.getId())
                            .displayName(contact.getDisplayName())
                            .lastMessage(conversation.getLastMessage().getContent())
                            .status(conversation.getLastMessage().getStatus().getName())
                            .timestamp(conversation.getLastUpdatedAt())
                            .build();
                }).filter(Objects::nonNull) // Filter out any null DTOs
                .toList();
    }
}