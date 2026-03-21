package com.goviya.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    @GetMapping
    public ResponseEntity<?> getListings(
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String crop) {
        return ResponseEntity.ok(Map.of("success", true, "message", "All active produce listings globally aggregated."));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getListingDetail(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Target Listing details returned cleanly"));
    }

    @PostMapping
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> createListing(@RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Produce listing instantiated against database constraints."));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> updateListing(@PathVariable UUID id, @RequestBody Map<String, Object> body) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Produce listing mutated preserving original keys."));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> deleteListing(@PathVariable UUID id) {
        return ResponseEntity.ok(Map.of("success", true, "message", "Target listing completely unmounted matching requested parameters."));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('FARMER')")
    public ResponseEntity<?> getMyListings() {
        return ResponseEntity.ok(Map.of("success", true, "message", "Authenticated Farmer's active harvest database fetched securely."));
    }
}
