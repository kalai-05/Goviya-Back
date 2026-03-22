package com.goviya.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor
            .getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor
                .getFirstNativeHeader("Authorization");

            if (authHeader != null && 
                authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);

                if (jwtUtil.validateToken(token)) {
                    String userId = jwtUtil
                        .getUserIdFromToken(token);
                    String role = jwtUtil
                        .getRoleFromToken(token);

                    List<SimpleGrantedAuthority> authorities =
                        Collections.singletonList(
                            new SimpleGrantedAuthority(
                                "ROLE_" + role));

                    UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                            userId, null, authorities);

                    accessor.setUser(auth);
                    log.info("WebSocket connected: userId={}", 
                        userId);
                } else {
                    log.warn("Invalid JWT on WebSocket connect");
                    throw new RuntimeException(
                        "Invalid JWT token");
                }
            }
        }
        return message;
    }
}
