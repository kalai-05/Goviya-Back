package com.goviya.controller;

import com.goviya.dto.ApiResponse;
import com.goviya.dto.CreateProductRequest;
import com.goviya.dto.CreateInquiryRequest;
import com.goviya.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    private String getCurrentUserId(Authentication authentication) {
        return authentication.getName();
    }

    @GetMapping("/")
    public ApiResponse<?> getShopsByDistrict(@RequestParam String district) {
        return ApiResponse.success(shopService.getShopsByDistrict(district));
    }

    @GetMapping("/{shopId}/products")
    public ApiResponse<?> getShopProducts(@PathVariable String shopId) {
        return ApiResponse.success(shopService.getShopProducts(shopId));
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('SHOP')")
    public ApiResponse<?> createProduct(@Valid @RequestBody CreateProductRequest request, Authentication authentication) {
        return ApiResponse.success(shopService.createProduct(getCurrentUserId(authentication), request));
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('SHOP')")
    public ApiResponse<?> updateProduct(@PathVariable String id, @Valid @RequestBody CreateProductRequest request, Authentication authentication) {
        return ApiResponse.success(shopService.updateProduct(id, getCurrentUserId(authentication), request));
    }

    @DeleteMapping("/products/{id}")
    @PreAuthorize("hasRole('SHOP')")
    public ApiResponse<?> deleteProduct(@PathVariable String id, Authentication authentication) {
        shopService.deleteProduct(id, getCurrentUserId(authentication));
        return ApiResponse.success("Product deleted successfully");
    }

    @PostMapping("/inquiries")
    public ApiResponse<?> createInquiry(@Valid @RequestBody CreateInquiryRequest request, Authentication authentication) {
        return ApiResponse.success(shopService.createInquiry(getCurrentUserId(authentication), request));
    }

    @GetMapping("/inquiries")
    @PreAuthorize("hasRole('SHOP')")
    public ApiResponse<?> getMyInquiries(Authentication authentication) {
        return ApiResponse.success(shopService.getShopInquiries(getCurrentUserId(authentication)));
    }
}
