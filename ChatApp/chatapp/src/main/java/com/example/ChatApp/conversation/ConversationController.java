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

@RestController
@RequestMapping("/api/conversations")
@RequiredArgsConstructor
public class ConversationController {

    private final ConversationService conversationService;
    private final UserRepository userRepository;

    @GetMapping
    public List<ConversationDto> getConversations(Principal principal) {
        String mobileNumber = principal.getName();
        User currentUser = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Conversation> conversations = conversationService.getConversationsForUser(currentUser);

        return conversations.stream().map(conversation -> {
            User contact = conversation.getUser1().equals(currentUser) ? conversation.getUser2() : conversation.getUser1();
            return ConversationDto.builder()
                    .contactId(contact.getId())
                    .displayName(contact.getDisplayName())
                    .lastMessage(conversation.getLastMessage().getContent())
                    .status(conversation.getLastMessage().getStatus().getName())
                    .timestamp(conversation.getLastUpdatedAt())
                    .build();
        }).toList();
    }
}

