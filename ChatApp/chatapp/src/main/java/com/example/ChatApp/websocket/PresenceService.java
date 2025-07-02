package com.example.ChatApp.websocket;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class PresenceService {

    // Using a thread-safe set to store user IDs
    private final Set<String> onlineUsers = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public void userOnline(String userId) {
        onlineUsers.add(userId);
        System.out.println("🟢 User online: " + userId + ". Total online: " + onlineUsers.size());
    }

    public void userOffline(String userId) {
        onlineUsers.remove(userId);
        System.out.println("🔴 User offline: " + userId + ". Total online: " + onlineUsers.size());
    }

    public Set<String> getOnlineUsers() {
        return Collections.unmodifiableSet(onlineUsers);
    }
}