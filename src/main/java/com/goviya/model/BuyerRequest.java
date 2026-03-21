package com.goviya.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "buyer_requests")
public class BuyerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "buyer_id", nullable = false)
    private User buyer;

    @Column(name = "crop_name")
    private String cropName;

    @Column(name = "quantity_kg")
    private Double quantityKg;

    @Column(name = "max_price_per_kg")
    private Double maxPricePerKg;

    private String district;
    private String description;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    public enum Status {
        OPEN, FILLED, CLOSED
    }
}
