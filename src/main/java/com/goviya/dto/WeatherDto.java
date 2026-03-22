package com.goviya.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherDto {
    private String district;
    private Double temperature;
    private String description;
    private Integer humidity;
    private Integer rainProbability;
    private String alert;
}
