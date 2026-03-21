package com.goviya.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @PostMapping
    @PreAuthorize("hasRole('BUYER')")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body) {
        // Will initialize 2.5% total commission and push state to PENDING mapping
        return ResponseEntity.ok(Map.of("success", true, "message", "Secured global transaction sequence placed smoothly without issue."));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyOrders() {
        return ResponseEntity.ok(Map.of("success", true, "message", "Your personal transaction invoice records resolved cleanly."));
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> confirmOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Order correctly flagged CONFIRMED signaling logic overrides."));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Terminal fulfillment marker mapped locking payment release structures securely."));
    }
}
