package com.example.ChatApp.websocket;

import com.example.ChatApp.message.dtos.UserStatusDto;
import com.example.ChatApp.message.service.MessageService; // ✅ Import MessageService
import com.example.ChatApp.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final PresenceService presenceService;
    private final MessageService messageService;
    private final UserService userService;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        Principal principal = event.getUser();
        if (principal == null) {
            log.warn("A user connected without authentication.");
            return;
        }

        String userId = principal.getName();
        presenceService.userOnline(userId);

        // Announce presence
        log.info("Broadcasting user ONLINE status for {}", userId);
        var publicStatusUpdate = UserStatusDto.builder()
                .userId(userId)
                .status(UserStatusDto.Status.ONLINE)
                .build();
        messagingTemplate.convertAndSend("/topic/presence", publicStatusUpdate);
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal != null) {
            String userId = principal.getName();
            presenceService.userOffline(userId);
            // ✅ Update the user's last seen timestamp in the database
            userService.updateLastSeen(userId);
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
