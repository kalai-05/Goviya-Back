package com.goviya.repository;

import com.goviya.model.BuyerRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface BuyerRequestRepository extends MongoRepository<BuyerRequest, String> {
    List<BuyerRequest> findByStatusAndDistrictOrderByCreatedAtDesc(String status, String district);
    List<BuyerRequest> findByBuyerIdOrderByCreatedAtDesc(String buyerId);
    List<BuyerRequest> findByStatusOrderByCreatedAtDesc(String status);
    long countByStatus(String status);
}
