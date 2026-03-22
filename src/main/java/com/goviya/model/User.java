package com.goviya.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Document(collection = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    private String id;

    @Indexed(unique = true)
    private String phone;

    private String name;
    private String role;       // "FARMER", "BUYER", "SHOP"
    private String district;
    private String language;   // "si", "ta", "en"
    private String fcmToken;
    private Float rating;
    private Integer totalDeals;
    private Double totalEarned;

    @CreatedDate
    private LocalDateTime createdAt;
}
