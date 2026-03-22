package com.goviya.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ChatMessageDto {
    private String id;
    private String senderId;
    private String senderName;
    private String senderRole;
    private String receiverId;
    private String message;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private String orderId;
}
