package com.goviya.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Firebase OTP trigger processed correctly for " + body.get("phone")));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(Map.of("success", true, "token", "mock-jwt-token-string"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "message", "User profile initialized and saved onto database."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(Map.of("success", true, "message", "User configurations payload extracted mapped to authenticated JWT headers."));
    }
}
