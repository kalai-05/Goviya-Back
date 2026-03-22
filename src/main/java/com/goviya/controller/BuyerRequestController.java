package com.goviya.controller;

import com.goviya.dto.ApiResponse;
import com.goviya.dto.CreateRequestRequest;
import com.goviya.dto.RespondToRequestRequest;
import com.goviya.service.BuyerRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class BuyerRequestController {

    private final BuyerRequestService requestService;

    private String getCurrentUserId(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping("/")
    public ApiResponse<?> getOpenRequests(@RequestParam String district) {
        return ApiResponse.success(requestService.getOpenRequests(district));
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse<?> createRequest(@Valid @RequestBody CreateRequestRequest request, Authentication authentication) {
        return ApiResponse.success(requestService.createRequest(getCurrentUserId(authentication), request));
    }

    @PostMapping("/{id}/respond")
    @PreAuthorize("hasRole('FARMER')")
    public ApiResponse<?> respondToRequest(@PathVariable String id, @Valid @RequestBody RespondToRequestRequest request, Authentication authentication) {
        return ApiResponse.success(requestService.respondToRequest(id, getCurrentUserId(authentication), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse<?> updateRequest(@PathVariable String id, @Valid @RequestBody CreateRequestRequest request, Authentication authentication) {
        return ApiResponse.success(requestService.updateRequest(id, getCurrentUserId(authentication), request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse<?> deleteRequest(@PathVariable String id, Authentication authentication) {
        requestService.deleteRequest(id, getCurrentUserId(authentication));
        return ApiResponse.success("Request deleted successfully");
    }

    @PutMapping("/responses/{responseId}/accept")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse<?> acceptResponse(@PathVariable String responseId, Authentication authentication) {
        return ApiResponse.success(requestService.acceptResponse(responseId, getCurrentUserId(authentication)));
    }

    @GetMapping("/my")
    public ApiResponse<?> getMyRequests(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .orElse("BUYER");
        return ApiResponse.success(requestService.getMyRequests(getCurrentUserId(authentication), role));
    }
}
