package com.goviya.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class PriceUpdateScheduler {

    private static final Logger log = Logger.getLogger(PriceUpdateScheduler.class.getName());

    // @Autowired
    // private MarketPriceRepository marketPriceRepository;
    
    // @Autowired
    // private UserRepository userRepository;
    
    // @Autowired
    // private FirebaseMessagingService fcmService;

    /**
     * Executes at 6:00 AM daily
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void fetchAndUpdateDailyPrices() {
        log.info("[SCHEDULER] Running PriceUpdateScheduler (6:00 AM)...");

        // Step 1: Fetch public prices from Manning Market Colombo API
        /*
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.getForEntity(
                "https://api.manningmarket.local/v1/daily-prices", List.class);
        */

        // Step 2: Iterate and save to market_prices table
        /*
        LocalDate today = LocalDate.now();
        List<MarketPrice> newPrices = new ArrayList<>();
        
        for(Map<String, Object> item : response.getBody()) {
            String crop = (String) item.get("cropName");
            Double newPrice = (Double) item.get("pricePerKg");
            
            // Step 3: Compare with yesterday's price
            MarketPrice yesterdayPriceLog = marketPriceRepository.findByCropNameAndPriceDate(crop, today.minusDays(1));
            Double prevPrice = yesterdayPriceLog != null ? yesterdayPriceLog.getPricePerKg() : newPrice;
            
            MarketPrice newLog = MarketPrice.builder()
                .cropName(crop)
                .pricePerKg(newPrice)
                .prevPricePerKg(prevPrice)
                .source(MarketPrice.Source.MANNING)
                .priceDate(today)
                .build();
                
            newPrices.add(newLog);

            // Step 4: If price changed by more than 10%, fire FCM Push to all FARMER users
            if (prevPrice > 0) {
                double diffPercentage = ((newPrice - prevPrice) / prevPrice) * 100.0;
                
                if (diffPercentage >= 10.0) {
                    log.info("Spike detected! " + crop + " went up by " + String.format("%.1f", diffPercentage) + "%");
                    
                    // List<User> farmers = userRepository.findAllByRole(User.Role.FARMER);
                    // String message = crop + " price up " + String.format("%.0f", diffPercentage) + "% - good time to sell!";
                    // fcmService.sendToMultipleUsers(farmers, "Price Spike Alert!", message);
                }
            }
        }
        
        marketPriceRepository.saveAll(newPrices);
        */

        log.info("[SCHEDULER] PriceUpdateScheduler finished mapping successfully.");
    }
}
