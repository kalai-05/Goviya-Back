package com.goviya.dto;

import lombok.Data;

@Data
public class CreateRequestRequest {
    private String cropName;
    private Double quantityKg;
    private Double maxPricePerKg;
    private String district;
    private String description;
}
