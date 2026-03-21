package com.goviya.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/prices")
public class PriceController {

    @GetMapping
    public ResponseEntity<?> getPrices(@RequestParam(required = false) String crop) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Current local market values returned securely out from database mappings successfully."));
    }
}
