package com.goviya.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "buyer_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BuyerRequest {
    @Id
    private String id;

    private String buyerId;
    private String buyerName;

    private String cropName;
    private Double quantityKg;
    private Double maxPricePerKg;
    private String district;
    private String description;

    private String status;  // "OPEN", "FILLED", "CLOSED"
    private LocalDateTime expiresAt;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;
}
