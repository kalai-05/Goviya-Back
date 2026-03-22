package com.goviya.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatMessageResponse {
    private String id;
    private String senderId;
    private String senderName;
    private String senderRole;
    private String receiverId;
    private String message;
    private String type;
    // TEXT, DEAL_PROPOSE, DEAL_ACCEPT, 
    // DEAL_REJECT, ORDER_UPDATE, PAYMENT_DONE
    private String orderId;
    private DealDetails dealDetails;
    private Boolean isRead;
    private LocalDateTime sentAt;
    private String conversationId; // smaller_id + "_" + larger_id
}
