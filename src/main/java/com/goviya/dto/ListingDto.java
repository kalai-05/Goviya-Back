package com.goviya.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ListingDto {
    private String id;
    private String farmerId;
    private String farmerName;
    private Double farmerRating;
    private String cropName;
    private Double quantityKg;
    private Double pricePerKg;
    private String district;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
    
    private Double distanceKm; // transient distance
}
