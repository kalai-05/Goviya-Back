package com.goviya.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDate;

@Document(collection = "market_prices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarketPrice {
    @Id
    private String id;

    private String cropName;
    private Double pricePerKg;
    private Double prevPricePerKg;
    private String source;  // "MANNING", "DAMBULLA"
    private LocalDate priceDate;
}
