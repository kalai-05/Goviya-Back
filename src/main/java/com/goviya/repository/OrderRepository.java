package com.goviya.repository;

import com.goviya.model.Order;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends MongoRepository<Order, String> {
    List<Order> findByBuyerIdOrderByCreatedAtDesc(String buyerId);
    List<Order> findByFarmerIdOrderByCreatedAtDesc(String farmerId);
    Optional<Order> findByPayhereRef(String payhereRef);
    List<Order> findByStatus(String status);
}
