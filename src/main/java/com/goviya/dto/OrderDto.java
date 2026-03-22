package com.goviya.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class OrderDto {
    private String id;
    private String buyerId;
    private String buyerName;
    private String farmerId;
    private String farmerName;
    private String listingId;
    private String cropName;
    private Double quantityKg;
    private Double totalPrice;
    private Double commission;
    private String status;
    private String paymentStatus;
    private LocalDateTime pickupDate;
    private LocalDateTime createdAt;
}
