package com.goviya.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderRequest {
    private String listingId;
    private Double quantityKg;
}
