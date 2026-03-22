package com.goviya.controller;

import com.goviya.dto.ApiResponse;
import com.goviya.dto.CreateOrderRequest;
import com.goviya.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private String getCurrentUserId(Authentication authentication) {
        return authentication.getName();
    }

    @PostMapping("/")
    @PreAuthorize("hasRole('BUYER')")
    public ApiResponse<?> createOrder(@Valid @RequestBody CreateOrderRequest request, Authentication authentication) {
        return ApiResponse.success(orderService.createOrder(getCurrentUserId(authentication), request.getListingId(), request.getQuantityKg()));
    }

    @GetMapping("/my")
    public ApiResponse<?> getMyOrders(Authentication authentication) {
        String role = authentication.getAuthorities().stream()
            .findFirst()
            .map(a -> a.getAuthority().replace("ROLE_", ""))
            .orElse("BUYER");
        return ApiResponse.success(orderService.getMyOrders(getCurrentUserId(authentication), role));
    }

    @PutMapping("/{id}/confirm")
    @PreAuthorize("hasRole('FARMER')")
    public ApiResponse<?> confirmOrder(@PathVariable String id, Authentication authentication) {
        return ApiResponse.success(orderService.confirmOrder(id, getCurrentUserId(authentication)));
    }

    @PutMapping("/{id}/complete")
    public ApiResponse<?> completeOrder(@PathVariable String id) {
        return ApiResponse.success(orderService.completeOrder(id));
    }
}
