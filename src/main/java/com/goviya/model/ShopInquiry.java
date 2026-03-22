package com.goviya.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "shop_inquiries")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopInquiry {
    @Id
    private String id;

    private String shopId;
    private String userId;
    private String userName;
    private String userRole;
    private String productId;
    private String productName;
    private String message;
    private String status;  // "NEW", "REPLIED", "CLOSED"

    @CreatedDate
    private LocalDateTime createdAt;
}
