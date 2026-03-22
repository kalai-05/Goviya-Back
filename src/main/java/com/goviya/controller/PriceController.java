package com.goviya.controller;

import com.goviya.dto.ApiResponse;
import com.goviya.service.PriceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/prices")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    private String getCurrentUserId(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping("/")
    public ApiResponse<?> getTodayPrices(@RequestParam(required = false) String crop) {
        return ApiResponse.success(priceService.getTodayPrices(crop));
    }
}
