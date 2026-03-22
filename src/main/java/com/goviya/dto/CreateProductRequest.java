package com.goviya.dto;

import lombok.Data;

@Data
public class CreateProductRequest {
    private String productName;
    private String category;
    private Double price;
    private String unit;
    private String stockStatus;
    private String imageUrl;
}
