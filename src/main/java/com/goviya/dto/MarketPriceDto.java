package com.goviya.dto;

import lombok.Data;

import java.time.LocalDateTime;


@Data
public class MarketPriceDto {
    private String id;
    private String cropName;
    private Double pricePerKg;
    private Double prevPricePerKg;
    private Double changePercent;
    private String source;
    private LocalDateTime priceDate;
}
