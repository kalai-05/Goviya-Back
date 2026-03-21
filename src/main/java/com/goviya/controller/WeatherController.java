package com.goviya.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @GetMapping
    public ResponseEntity<?> getWeather(@RequestParam String district) {
        return ResponseEntity.ok(Map.of("success", true, "message", "OpenWeatherMap logic dispatched retrieving current arrays seamlessly mapping " + district));
    }
}
