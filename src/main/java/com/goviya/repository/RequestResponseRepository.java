package com.goviya.repository;

import com.goviya.model.RequestResponse;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface RequestResponseRepository extends MongoRepository<RequestResponse, String> {
    List<RequestResponse> findByRequestId(String requestId);
    Optional<RequestResponse> findByRequestIdAndFarmerId(String requestId, String farmerId);
    List<RequestResponse> findByRequestIdAndStatusNot(String requestId, String status);
    long countByRequestId(String requestId);
}
