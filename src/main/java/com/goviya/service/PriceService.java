package com.goviya.service;

import com.goviya.dto.MarketPriceDto;
import com.goviya.model.MarketPrice;
import com.goviya.repository.MarketPriceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PriceService {

    private final MarketPriceRepository marketPriceRepository;

    public List<MarketPriceDto> getTodayPrices(String crop) {
        LocalDate today = LocalDate.now();
        List<MarketPrice> prices;

        if (crop != null && !crop.isEmpty()) {
            MarketPrice price = marketPriceRepository.findByCropNameAndPriceDate(crop, today).orElse(null);
            prices = price != null ? java.util.List.of(price) : java.util.Collections.emptyList();
        } else {
            prices = marketPriceRepository.findByPriceDate(today);
        }

        return prices.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    public void savePrices(List<MarketPriceDto> incomingPrices) {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        for (MarketPriceDto dto : incomingPrices) {
            MarketPrice existing = marketPriceRepository.findByCropNameAndPriceDate(dto.getCropName(), today).stream().findFirst().orElse(new MarketPrice());

            existing.setCropName(dto.getCropName());
            existing.setPricePerKg(dto.getPricePerKg());
            existing.setSource(dto.getSource());
            existing.setPriceDate(today);

            marketPriceRepository.findByCropNameAndPriceDate(dto.getCropName(), yesterday).stream().findFirst().ifPresent(prev -> {
                existing.setPrevPricePerKg(prev.getPricePerKg());
            });

            marketPriceRepository.save(existing);
        }
    }

    private MarketPriceDto mapToDto(MarketPrice entity) {
        MarketPriceDto dto = new MarketPriceDto();
        dto.setId(entity.getId());
        dto.setCropName(entity.getCropName());
        dto.setPricePerKg(entity.getPricePerKg());
        dto.setPrevPricePerKg(entity.getPrevPricePerKg());
        dto.setSource(entity.getSource());
        
        // Use LocalDate for simplicity or match entity type. Assuming LocalDate here per logic
        dto.setPriceDate(entity.getPriceDate() != null ? entity.getPriceDate().atStartOfDay() : null);

        if (entity.getPricePerKg() != null && entity.getPrevPricePerKg() != null && entity.getPrevPricePerKg() > 0) {
            double change = ((entity.getPricePerKg() - entity.getPrevPricePerKg()) / entity.getPrevPricePerKg()) * 100;
            dto.setChangePercent(Math.round(change * 100.0) / 100.0);
        }
        
        return dto;
    }
}
