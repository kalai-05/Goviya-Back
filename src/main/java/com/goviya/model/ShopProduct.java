package com.goviya.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "shop_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopProduct {
    @Id
    private String id;

    private String shopId;
    private String shopName;

    private String productName;
    private String category;   // "FERTILIZER","SEED","PESTICIDE","TOOL"
    private Double price;
    private String unit;
    private String stockStatus; // "IN", "LOW", "OUT"
    private String imageUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;
}
