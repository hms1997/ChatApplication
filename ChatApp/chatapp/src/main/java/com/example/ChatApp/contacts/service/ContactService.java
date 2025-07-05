package com.example.ChatApp.contacts.service;

import com.example.ChatApp.contacts.dto.ContactResponse;
import com.example.ChatApp.entities.User;
import com.example.ChatApp.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactService {

    private final UserRepository userRepository;

    public ContactService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<ContactResponse> syncContacts(List<String> mobileNumbers) {
        List<User> users = userRepository.findByMobileNumberIn(mobileNumbers);

        return users.stream()
                .map(user -> ContactResponse.builder()
                        .userId(user.getId()) // ✅ Explicitly set userId
                        .mobileNumber(user.getMobileNumber())
                        .displayName(user.getDisplayName())
                        .lastSeen(user.getLastSeen())
                        .build())
                .toList();
    }
}