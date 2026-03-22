package com.goviya.dto;

import lombok.Data;



@Data
public class SendMessageRequest {
    private String receiverId;
    private String message;
    private String orderId;
}
