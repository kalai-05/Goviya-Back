package com.goviya.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;

    private String buyerId;
    private String buyerName;
    private String buyerPhone;

    private String farmerId;
    private String farmerName;
    private String farmerPhone;

    private String listingId;
    private String cropName;
    private Double quantityKg;
    private Double totalPrice;
    private Double commission;

    private String status;        // "PENDING","CONFIRMED","DONE"
    private String paymentStatus; // "UNPAID","PAID","REFUNDED"
    private String payhereRef;

    private LocalDateTime pickupDate;

    @CreatedDate
    private LocalDateTime createdAt;
}
