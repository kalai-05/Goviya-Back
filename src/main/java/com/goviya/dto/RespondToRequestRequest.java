package com.goviya.dto;

import lombok.Data;

@Data
public class RespondToRequestRequest {
    private Double offeredPricePerKg;
    private Double quantityKg;
    private String message;
}
