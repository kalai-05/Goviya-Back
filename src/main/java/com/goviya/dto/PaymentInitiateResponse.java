package com.goviya.dto;

import lombok.Data;



@Data
public class PaymentInitiateResponse {
    private String merchantId;
    private String orderId;
    private Double amount;
    private String currency;
    private String orderDescription;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String hash;
}
