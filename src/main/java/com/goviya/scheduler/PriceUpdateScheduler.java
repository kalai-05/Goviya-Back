package com.goviya.scheduler;

import com.goviya.dto.MarketPriceDto;
import com.goviya.service.NotificationService;
import com.goviya.service.PriceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class PriceUpdateScheduler {

    private final PriceService priceService;
    private final NotificationService notificationService;
    private final RestTemplate restTemplate;

    @Scheduled(cron = "0 0 6 * * *")
    public void updateMarketPrices() {
        log.info("Starting scheduled market price update...");
        
        Map<String, Double> prices = new HashMap<>();
        prices.put("Tomato", generateRealisticPrice(60.0, 120.0));
        prices.put("Cabbage", generateRealisticPrice(40.0, 80.0));
        prices.put("Onion", generateRealisticPrice(100.0, 200.0));
        prices.put("Chilli", generateRealisticPrice(200.0, 400.0));
        prices.put("Carrot", generateRealisticPrice(60.0, 100.0));
        prices.put("Garlic", generateRealisticPrice(300.0, 500.0));
        prices.put("Potato", generateRealisticPrice(80.0, 140.0));
        prices.put("Leeks", generateRealisticPrice(100.0, 200.0));
        prices.put("Beans", generateRealisticPrice(120.0, 240.0));
        prices.put("Beet", generateRealisticPrice(50.0, 90.0));

        List<MarketPriceDto> dtos = new ArrayList<>();
        prices.forEach((crop, price) -> {
            MarketPriceDto dto = new MarketPriceDto();
            dto.setCropName(crop);
            dto.setPricePerKg(price);
            dto.setSource("Manning Market (Simulated)");
            dtos.add(dto);
        });

        // Save all prices
        priceService.savePrices(dtos);
        
        // Fetch to check change percent after save
        List<MarketPriceDto> latestPrices = priceService.getTodayPrices(null);
        
        for (MarketPriceDto latest : latestPrices) {
            if (latest.getChangePercent() != null && latest.getChangePercent() >= 10.0) {
                notificationService.notifyPriceAlert(latest.getCropName(), latest.getChangePercent());
            }
        }

        log.info("Market price update completed successfully.");
    }

    private Double generateRealisticPrice(double min, double max) {
        double val = min + (Math.random() * (max - min));
        return Math.round(val / 5.0) * 5.0;
    }
}
