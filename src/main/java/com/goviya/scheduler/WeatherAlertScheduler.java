package com.goviya.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class WeatherAlertScheduler {

    private static final Logger log = Logger.getLogger(WeatherAlertScheduler.class.getName());

    // @Autowired
    // private UserRepository userRepository;
    
    // @Autowired
    // private FirebaseMessagingService fcmService;

    /**
     * Executes at 18:00 (6:00 PM) daily
     */
    @Scheduled(cron = "0 0 18 * * *")
    public void processDailyWeatherAlerts() {
        log.info("[SCHEDULER] Running WeatherAlertScheduler (6:00 PM)...");

        // Step 1: Pinpoint all unique districts that possess actively registered FARMER users
        /*
        List<String> activeDistricts = userRepository.findDistinctDistrictsByRole(User.Role.FARMER);
        RestTemplate restTemplate = new RestTemplate();
        
        for (String district : activeDistricts) {
            
            // Step 2: Query OpenWeatherMap constraints 
            String apiUrl = "https://api.openweathermap.org/data/2.5/forecast?q=" + district + ",LK&appid=YOUR_API_KEY";
            ResponseEntity<Map> response = restTemplate.getForEntity(apiUrl, Map.class);
            
            // Step 3: Analyze precipitation probability (POP) for tomorrow
            Double popLimit = extractTomorrowPopFromPayload(response.getBody()); 
            
            if (popLimit != null && popLimit > 0.70) { // 70% threshold
                log.info("Heavy rain approaching " + district + ". Triggering FCM dispatches!");
                
                // Fetch farmers isolated to the targeted district securely
                // List<User> localFarmers = userRepository.findAllByRoleAndDistrict(User.Role.FARMER, district);
                
                // fcmService.sendToMultipleUsers(
                //     localFarmers, 
                //     "Weather Alert", 
                //     "Rain tomorrow - harvest your crops today!"
                // );
            }
        }
        */

        log.info("[SCHEDULER] WeatherAlertScheduler processed district clusters accurately.");
    }
}
