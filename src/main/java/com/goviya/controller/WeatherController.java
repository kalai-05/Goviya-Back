package com.goviya.controller;

import com.goviya.dto.ApiResponse;
import com.goviya.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    private String getCurrentUserId(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping("/")
    public ApiResponse<?> getWeather(@RequestParam String district) {
        return ApiResponse.success(weatherService.getWeather(district));
    }
}
