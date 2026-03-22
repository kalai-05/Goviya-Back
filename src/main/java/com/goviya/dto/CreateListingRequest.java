package com.goviya.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateListingRequest {
    private String cropName;
    private Double quantityKg;
    private Double pricePerKg;
    private String district;
    private String description;
    private String imageUrl;
    private LocalDateTime expiresAt;
}
