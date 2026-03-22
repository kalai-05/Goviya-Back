package com.goviya.dto;

import lombok.Data;



@Data
public class CreateInquiryRequest {
    private String shopId;
    private String productId;
    private String productName;
    private String message;
}
