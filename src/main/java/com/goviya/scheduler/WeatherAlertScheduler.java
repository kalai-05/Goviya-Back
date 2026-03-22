package com.goviya.scheduler;

import com.goviya.dto.WeatherDto;
import com.goviya.model.User;
import com.goviya.repository.UserRepository;
import com.goviya.service.NotificationService;
import com.goviya.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class WeatherAlertScheduler {

    private final WeatherService weatherService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    private static final List<String> DISTRICTS = Arrays.asList(
            "Colombo", "Kandy", "Galle", "Anuradhapura", "Jaffna",
            "Matara", "Kurunegala", "Ratnapura", "Badulla",
            "Trincomalee", "Batticaloa", "Ampara", "Polonnaruwa",
            "Mannar", "Vavuniya", "Mullaitivu", "Kilinochchi",
            "Puttalam", "Hambantota", "Monaragala", "Kegalle",
            "Nuwara Eliya", "Kalutara", "Gampaha", "Matale"
    );

    @Scheduled(cron = "0 0 18 * * *")
    public void sendWeatherAlerts() {
        log.info("Starting scheduled weather alerts...");

        for (String district : DISTRICTS) {
            try {
                List<User> farmersInDistrict = userRepository.findByRoleAndDistrict("FARMER", district);
                if (farmersInDistrict.isEmpty()) {
                    continue; // skip (no farmers there)
                }

                WeatherDto weather = weatherService.getWeather(district);

                if (weather.getRainProbability() != null && weather.getRainProbability() > 70) {
                    notificationService.notifyFarmersInDistrict(
                            district,
                            "Rain Alert! ⛈",
                            "Heavy rain expected tomorrow in " + district + ". Harvest your crops today!"
                    );
                    log.info("Rain alert triggered and sent for district: {}", district);
                } else {
                    log.info("Weather checked for {}: No heavy rain (probability: {}%)", district, weather.getRainProbability());
                }

            } catch (Exception e) {
                log.error("Exception handling weather alert for district: {}", district, e);
            }
        }

        log.info("Weather alerts scheduling cycle completed.");
    }
}
