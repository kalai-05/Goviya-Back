package com.goviya.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "produce_listings")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Listing {
    @Id
    private String id;

    private String farmerId;
    private String farmerName;
    private Float farmerRating;

    private String cropName;
    private Double quantityKg;
    private Double pricePerKg;
    private String district;
    private Double latitude;
    private Double longitude;
    private String imageUrl;
    private String description;

    private String status;  // "ACTIVE", "SOLD", "EXPIRED"
    private LocalDateTime expiresAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;
}
