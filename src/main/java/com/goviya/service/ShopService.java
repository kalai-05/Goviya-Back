package com.goviya.service;

import com.goviya.dto.CreateProductRequest;
import com.goviya.dto.CreateInquiryRequest;
import com.goviya.model.ShopProduct;
import com.goviya.model.ShopInquiry;
import com.goviya.model.User;
import com.goviya.repository.ShopProductRepository;
import com.goviya.repository.ShopInquiryRepository;
import com.goviya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopProductRepository shopProductRepository;
    private final UserRepository userRepository;

    public List<ShopProduct> getShopProducts(String shopId) {
        return shopProductRepository.findByShopIdOrderByCreatedAtDesc(shopId);
    }

    public ShopProduct createProduct(String shopId, CreateProductRequest request) {
        User shop = userRepository.findById(shopId).orElseThrow(() -> new RuntimeException("Shop not found"));
        
        ShopProduct product = new ShopProduct();
        product.setShopId(shop.getId());
        product.setShopName(shop.getName());
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        product.setStockStatus(request.getStockStatus() != null ? request.getStockStatus() : "IN_STOCK");
        product.setImageUrl(request.getImageUrl());
        
        return shopProductRepository.save(product);
    }

    public ShopProduct updateProduct(String id, String shopId, CreateProductRequest request) {
        ShopProduct product = shopProductRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getShopId().equals(shopId)) {
            throw new RuntimeException("Not authorized");
        }
        
        product.setProductName(request.getProductName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setUnit(request.getUnit());
        if (request.getStockStatus() != null) product.setStockStatus(request.getStockStatus());
        if (request.getImageUrl() != null) product.setImageUrl(request.getImageUrl());
        
        return shopProductRepository.save(product);
    }
    
    public void deleteProduct(String id, String shopId) {
        ShopProduct product = shopProductRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        if (!product.getShopId().equals(shopId)) {
            throw new RuntimeException("Not authorized");
        }
        shopProductRepository.delete(product);
    }

    private final ShopInquiryRepository shopInquiryRepository;

    public List<User> getShopsByDistrict(String district) {
        if (district == null || district.isEmpty()) {
            return userRepository.findByRole("SHOP");
        }
        return userRepository.findByRoleAndDistrict("SHOP", district);
    }

    public ShopInquiry createInquiry(String userId, CreateInquiryRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        
        ShopInquiry inquiry = new ShopInquiry();
        inquiry.setUserId(user.getId());
        inquiry.setUserName(user.getName());
        inquiry.setUserRole(user.getRole());
        inquiry.setShopId(request.getShopId());
        inquiry.setProductId(request.getProductId());
        inquiry.setProductName(request.getProductName());
        inquiry.setMessage(request.getMessage());
        inquiry.setStatus("NEW");
        
        return shopInquiryRepository.save(inquiry);
    }

    public List<ShopInquiry> getShopInquiries(String shopId) {
        return shopInquiryRepository.findByShopIdOrderByCreatedAtDesc(shopId);
    }
}
