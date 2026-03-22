package com.goviya.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class BuyerRequestDto {
    private String id;
    private String buyerId;
    private String buyerName;
    private String cropName;
    private Double quantityKg;
    private Double maxPricePerKg;
    private String district;
    private String description;
    private String status;
    private Integer responseCount;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
