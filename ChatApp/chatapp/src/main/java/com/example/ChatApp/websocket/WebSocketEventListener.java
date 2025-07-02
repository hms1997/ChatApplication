package com.example.ChatApp.websocket;

import com.example.ChatApp.message.dtos.UserStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j // Add logging for easier debugging
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final PresenceService presenceService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        Principal principal = event.getUser();
        if (principal == null) {
            log.warn("A user connected without authentication.");
            return;
        }

        String userId = principal.getName();
        presenceService.userOnline(userId);

        // 1. Announce to everyone that this user is now online
        log.info("Broadcasting user ONLINE status for {}", userId);
        var publicStatusUpdate = UserStatusDto.builder()
                .userId(userId)
                .status(UserStatusDto.Status.ONLINE)
                .build();
        messagingTemplate.convertAndSend("/topic/presence", publicStatusUpdate);

        // 2. ✅ BACKFILL: Send the full list of online users ONLY to the connecting user
        log.info("Back filling online users list for user {}", userId);
        presenceService.getOnlineUsers().forEach(onlineUserId -> {
            var privateStatusUpdate = UserStatusDto.builder()
                    .userId(onlineUserId)
                    .status(UserStatusDto.Status.ONLINE)
                    .build();
            // Send to the user's private queue
            messagingTemplate.convertAndSendToUser(
                    userId,             // The user who just connected
                    "/queue/presence",  // A private destination for them
                    privateStatusUpdate
            );
        });
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Principal principal = event.getUser();

        if (principal != null) {
            String userId = principal.getName();
            presenceService.userOffline(userId);

            log.info("Broadcasting user OFFLINE status for {}", userId);
            var statusUpdate = UserStatusDto.builder()
                    .userId(userId)
                    .status(UserStatusDto.Status.OFFLINE)
                    .build();

            messagingTemplate.convertAndSend("/topic/presence", statusUpdate);
        } else {
            log.warn("A user disconnected without authentication information.");
        }
    }
}