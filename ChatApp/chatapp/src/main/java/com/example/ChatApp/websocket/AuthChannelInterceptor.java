package com.example.ChatApp.websocket;

import com.example.ChatApp.auth.utils.JwtUtil;
import com.example.ChatApp.entities.User;
import com.example.ChatApp.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // We only care about the initial CONNECT command
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // "Authorization" is a custom header we're sending from the client
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            log.debug("WebSocket CONNECT Authorization header: {}", authHeader);

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    String userId = jwtUtil.extractUserId(token);
                    User user = userRepository.findById(userId).orElse(null);

                    if (user != null) {
                        // If valid, set the user for this WebSocket session
                        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                                user.getId(), null, List.of());
                        accessor.setUser(auth);
                        log.info("Authenticated WebSocket user: {}", user.getId());
                        return message;
                    }
                }
            }
            // If we're here, authentication failed. You could throw an exception
            // to reject the connection, but for now we'll log and let it connect anonymously.
            log.warn("WebSocket connection failed authentication.");
        }
        return message;
    }
}