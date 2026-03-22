package com.goviya.service;

import com.goviya.dto.ChatMessageDto;
import com.goviya.model.ChatMessage;
import com.goviya.model.User;
import com.goviya.repository.ChatMessageRepository;
import com.goviya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public List<ChatMessageDto> getConversation(String userId1, String userId2) {
        return chatMessageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderBySentAtAsc(userId1, userId2, userId2, userId1)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ChatMessageDto sendMessage(String senderId, String receiverId, String messageContent, String orderId) {
        User sender = userRepository.findById(senderId).orElseThrow(() -> new RuntimeException("Sender not found"));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new RuntimeException("Receiver not found"));
        
        ChatMessage msg = new ChatMessage();
        msg.setSenderId(sender.getId());
        msg.setSenderName(sender.getName());
        msg.setSenderRole(sender.getRole());
        msg.setReceiverId(receiver.getId());
        msg.setMessage(messageContent);
        msg.setOrderId(orderId);
        msg.setIsRead(false);
        msg.setSentAt(LocalDateTime.now());
        
        msg = chatMessageRepository.save(msg);
        
        notificationService.sendToUser(receiverId, "New Message from " + sender.getName(), messageContent);
        return mapToDto(msg);
    }

    public void markAsRead(String receiverId, String senderId) {
        List<ChatMessage> unread = chatMessageRepository.findByReceiverIdAndSenderIdAndIsReadFalse(receiverId, senderId);
        unread.forEach(m -> m.setIsRead(true));
        chatMessageRepository.saveAll(unread);
    }

    private ChatMessageDto mapToDto(ChatMessage msg) {
        ChatMessageDto dto = new ChatMessageDto();
        dto.setId(msg.getId());
        dto.setSenderId(msg.getSenderId());
        dto.setSenderName(msg.getSenderName());
        dto.setSenderRole(msg.getSenderRole());
        dto.setReceiverId(msg.getReceiverId());
        dto.setMessage(msg.getMessage());
        dto.setIsRead(msg.getIsRead());
        dto.setSentAt(msg.getSentAt());
        dto.setOrderId(msg.getOrderId());
        return dto;
    }
}
