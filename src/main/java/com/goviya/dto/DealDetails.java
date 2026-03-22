package com.goviya.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DealDetails {
    private String cropName;
    private Double quantityKg;
    private Double pricePerKg;
    private Double totalPrice;
    private String pickupDistrict;
    private String pickupDate;
}
