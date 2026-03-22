package com.goviya.repository;

import com.goviya.model.Listing;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface ListingRepository extends MongoRepository<Listing, String> {
    List<Listing> findByStatusAndDistrictOrderByCreatedAtDesc(String status, String district);
    List<Listing> findByStatusAndDistrictAndCropNameContainingIgnoreCaseOrderByCreatedAtDesc(
        String status, String district, String crop);
    List<Listing> findByFarmerIdOrderByCreatedAtDesc(String farmerId);
    List<Listing> findByStatusAndExpiresAtBefore(String status, LocalDateTime now);
    List<Listing> findByStatusAndCropNameContainingIgnoreCaseOrderByCreatedAtDesc(String status, String crop);
    List<Listing> findByStatusOrderByCreatedAtDesc(String status);
}
