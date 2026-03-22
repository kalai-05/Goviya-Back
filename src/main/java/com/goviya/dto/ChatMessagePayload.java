package com.goviya.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ChatMessagePayload {
    private String receiverId;
    private String message;
    private String type;
    // types: TEXT, DEAL_PROPOSE, DEAL_ACCEPT, 
    //        DEAL_REJECT, ORDER_UPDATE
    private String orderId;        // nullable
    private DealDetails dealDetails; // nullable, for DEAL_PROPOSE
}
