package com.goviya.controller;

import com.goviya.dto.ApiResponse;
import com.goviya.model.ChatMessage;
import com.goviya.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatRestController {

    private final ChatMessageRepository chatMessageRepository;

    // Load conversation history between 2 users
    @GetMapping("/history/{otherUserId}")
    public ResponseEntity<ApiResponse<List<ChatMessage>>> getHistory(
            @PathVariable String otherUserId,
            Authentication auth
    ) {
        String myId = auth.getName();

        List<ChatMessage> messages = chatMessageRepository
            .findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderBySentAtAsc(
                myId, otherUserId, otherUserId, myId);

        return ResponseEntity.ok(ApiResponse.success(messages));
    }

    // Get all conversations (chat list screen)
    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getConversations(Authentication auth) {
        String myId = auth.getName();

        // Get all messages involving me
        List<ChatMessage> allMsgs = chatMessageRepository.findByReceiverIdOrSenderId(myId, myId);

        // Group by conversation partner → get latest message
        Map<String, ChatMessage> latestByPartner = new LinkedHashMap<>();

        allMsgs.stream()
            .sorted(Comparator.comparing(ChatMessage::getSentAt).reversed())
            .forEach(msg -> {
                String partnerId = msg.getSenderId().equals(myId)
                    ? msg.getReceiverId()
                    : msg.getSenderId();

                latestByPartner.putIfAbsent(partnerId, msg);
            });

        List<Map<String, Object>> conversations = latestByPartner.entrySet().stream()
            .map(e -> {
                Map<String, Object> conv = new LinkedHashMap<>();
                ChatMessage lastMsg = e.getValue();
                conv.put("partnerId", e.getKey());
                // If I am the sender, we show current placeholder or fetch from user repo (client side)
                // If I am the receiver, we have the sender's info in the message
                conv.put("partnerName", lastMsg.getSenderId().equals(myId) ? "Recipient" : lastMsg.getSenderName());
                conv.put("partnerRole", lastMsg.getSenderId().equals(myId) ? "USER" : lastMsg.getSenderRole());
                conv.put("lastMessage", lastMsg.getMessage());
                conv.put("lastMessageType", lastMsg.getType());
                conv.put("lastMessageTime", lastMsg.getSentAt());
                conv.put("unreadCount", chatMessageRepository.countByReceiverIdAndSenderIdAndIsReadFalse(myId, e.getKey()));
                return conv;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success(conversations));
    }

    // Get unread count for badge
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadCount(Authentication auth) {
        String myId = auth.getName();
        long count = chatMessageRepository.countByReceiverIdAndIsReadFalse(myId);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
}
