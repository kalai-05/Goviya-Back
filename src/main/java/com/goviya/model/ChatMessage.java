package com.goviya.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    @Id
    private String id;

    private String senderId;
    private String senderName;
    private String senderRole;

    private String receiverId;
    private String message;
    private Boolean isRead;
    private String orderId;

    private LocalDateTime sentAt;
}
