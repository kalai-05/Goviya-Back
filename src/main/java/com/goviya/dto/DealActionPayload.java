package com.goviya.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DealActionPayload {
    private String messageId;  // the DEAL_PROPOSE message id
    private String orderId;    // created after accept
    private String action;     // "ACCEPT" or "REJECT"
}
