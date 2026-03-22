package com.goviya.repository;

import com.goviya.model.ShopProduct;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ShopProductRepository extends MongoRepository<ShopProduct, String> {
    List<ShopProduct> findByShopIdOrderByCreatedAtDesc(String shopId);
    List<ShopProduct> findByShopIdAndStockStatus(String shopId, String stockStatus);
    List<ShopProduct> findByCategory(String category);
}
