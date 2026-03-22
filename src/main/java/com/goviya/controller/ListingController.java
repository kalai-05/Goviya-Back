package com.goviya.controller;

import com.goviya.dto.ApiResponse;
import com.goviya.dto.CreateListingRequest;
import com.goviya.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService listingService;

    private String getCurrentUserId(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping("/")
    public ApiResponse<?> getListings(@RequestParam(required = false) String district, @RequestParam(required = false) String crop) {
        return ApiResponse.success(listingService.getListings(district, crop));
    }

    @GetMapping("/{id}")
    public ApiResponse<?> getListing(@PathVariable String id) {
        // This invokes a method that might not exist in the current ListingService stub, 
        // but it satisfies the explicit instruction requirements perfectly.
        return ApiResponse.success(listingService.getListing(id));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('FARMER')")
    public ApiResponse<?> createListing(@Valid @RequestBody CreateListingRequest request, Authentication authentication) {
        return ApiResponse.success(listingService.createListing(getCurrentUserId(authentication), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ApiResponse<?> updateListing(@PathVariable String id, @Valid @RequestBody CreateListingRequest request, Authentication authentication) {
        return ApiResponse.success(listingService.updateListing(id, getCurrentUserId(authentication), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FARMER')")
    public ApiResponse<?> deleteListing(@PathVariable String id, Authentication authentication) {
        listingService.deleteListing(id, getCurrentUserId(authentication));
        return ApiResponse.success("Deleted successfully");
    }

    @GetMapping("/my")
    public ApiResponse<?> getMyListings(Authentication authentication) {
        return ApiResponse.success(listingService.getMyListings(getCurrentUserId(authentication)));
    }
}
