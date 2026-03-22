package com.goviya.repository;

import com.goviya.model.ShopInquiry;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ShopInquiryRepository extends MongoRepository<ShopInquiry, String> {
    List<ShopInquiry> findByShopIdOrderByCreatedAtDesc(String shopId);
    List<ShopInquiry> findByShopIdAndStatus(String shopId, String status);
    long countByShopIdAndStatus(String shopId, String status);
}
