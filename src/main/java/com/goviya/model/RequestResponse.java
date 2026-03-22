package com.goviya.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "request_responses")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestResponse {
    @Id
    private String id;

    private String requestId;
    private String farmerId;
    private String farmerName;

    private Double offeredPricePerKg;
    private Double quantityKg;
    private String message;

    private String status;  // "PENDING", "ACCEPTED", "DECLINED"

    @CreatedDate
    private LocalDateTime createdAt;
}
