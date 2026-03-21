package com.goviya.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "market_prices")
public class MarketPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "crop_name", nullable = false)
    private String cropName;

    @Column(name = "price_per_kg")
    private Double pricePerKg;

    @Column(name = "prev_price_per_kg")
    private Double prevPricePerKg;

    @Enumerated(EnumType.STRING)
    private Source source;

    @Column(name = "price_date")
    private LocalDate priceDate;

    public enum Source {
        MANNING, DAMBULLA
    }
}
