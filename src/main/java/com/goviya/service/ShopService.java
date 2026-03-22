package com.goviya.service;

import com.goviya.dto.CreateProductRequest;
import com.goviya.dto.CreateInquiryRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ShopService {

    public List<Object> getShopsByDistrict(String district) {
        return new ArrayList<>();
    }

    public List<Object> getShopProducts(String shopId) {
        return new ArrayList<>();
    }

    public Object createProduct(String shopId, CreateProductRequest request) {
        return new Object();
    }

    public Object updateProduct(String id, String shopId, CreateProductRequest request) {
        return new Object();
    }

    public Object createInquiry(String userId, CreateInquiryRequest request) {
        return new Object();
    }

    public List<Object> getShopInquiries(String shopId) {
        return new ArrayList<>();
    }
}
