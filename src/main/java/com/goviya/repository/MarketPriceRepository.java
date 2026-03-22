package com.goviya.repository;

import com.goviya.model.MarketPrice;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MarketPriceRepository extends MongoRepository<MarketPrice, String> {
    List<MarketPrice> findByPriceDate(LocalDate date);
    Optional<MarketPrice> findByCropNameAndPriceDate(String cropName, LocalDate date);
    List<MarketPrice> findByCropNameOrderByPriceDateDesc(String cropName);
}
