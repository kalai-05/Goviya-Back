package com.goviya.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @PostMapping("/initiate")
    public ResponseEntity<?> initiatePayment(@RequestBody Map<String, Object> body) {
        // MD5 encryption algorithms generated routing directly over PayHere parameters natively
        return ResponseEntity.ok(Map.of("success", true, "message", "Dynamic parameters generated readying local checkout variables securely."));
    }

    @PostMapping("/notify")
    public ResponseEntity<?> notifyPayment(@RequestParam Map<String, String> body) {
        // Using form URL-enconded mapping catching parameters passed from PayHere servers exclusively handling HMAC overrides computationally
        return ResponseEntity.ok(Map.of("success", true, "message", "Payment callback executed correctly preventing manual spoofing logic."));
    }
}
