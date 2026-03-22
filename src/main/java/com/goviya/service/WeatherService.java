package com.goviya.service;

import com.goviya.dto.WeatherDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final RestTemplate restTemplate;

    @Value("${openweather.api-key}")
    private String apiKey;

    @Value("${openweather.base-url}")
    private String baseUrl;

    private static final Map<String, double[]> DISTRICT_COORDS = new HashMap<>();

    static {
        DISTRICT_COORDS.put("Colombo", new double[]{6.9271, 79.8612});
        DISTRICT_COORDS.put("Kandy", new double[]{7.2906, 80.6337});
        DISTRICT_COORDS.put("Galle", new double[]{6.0535, 80.2210});
        DISTRICT_COORDS.put("Anuradhapura", new double[]{8.3114, 80.4037});
        DISTRICT_COORDS.put("Jaffna", new double[]{9.6615, 80.0255});
        DISTRICT_COORDS.put("Matara", new double[]{5.9549, 80.5550});
        DISTRICT_COORDS.put("Kurunegala", new double[]{7.4818, 80.3609});
        DISTRICT_COORDS.put("Ratnapura", new double[]{6.6828, 80.3992});
        DISTRICT_COORDS.put("Badulla", new double[]{6.9934, 81.0550});
        DISTRICT_COORDS.put("Trincomalee", new double[]{8.5922, 81.2152});
        DISTRICT_COORDS.put("Gampaha", new double[]{7.0865, 79.9983});
        DISTRICT_COORDS.put("Kalutara", new double[]{6.5854, 79.9607});
        DISTRICT_COORDS.put("Matale", new double[]{7.4660, 80.6220});
        DISTRICT_COORDS.put("Nuwara Eliya", new double[]{6.9687, 80.7828});
        DISTRICT_COORDS.put("Hambantota", new double[]{6.1248, 81.1221});
        DISTRICT_COORDS.put("Kilinochchi", new double[]{9.3803, 80.3770});
        DISTRICT_COORDS.put("Mannar", new double[]{8.9804, 79.9048});
        DISTRICT_COORDS.put("Vavuniya", new double[]{8.7514, 80.4971});
        DISTRICT_COORDS.put("Mullaitivu", new double[]{9.2671, 80.8142});
        DISTRICT_COORDS.put("Batticaloa", new double[]{7.7102, 81.6924});
        DISTRICT_COORDS.put("Ampara", new double[]{7.2912, 81.6724});
        DISTRICT_COORDS.put("Puttalam", new double[]{8.0362, 79.8283});
        DISTRICT_COORDS.put("Polonnaruwa", new double[]{7.9403, 81.0188});
        DISTRICT_COORDS.put("Monaragala", new double[]{6.8728, 81.3507});
        DISTRICT_COORDS.put("Kegalle", new double[]{7.2513, 80.3464});
    }

    public WeatherDto getWeather(String district) {
        if (!DISTRICT_COORDS.containsKey(district)) {
            throw new IllegalArgumentException("District not found: " + district);
        }
        double[] coords = DISTRICT_COORDS.get(district);

        String weatherUrl = String.format("%s/weather?lat=%s&lon=%s&appid=%s&units=metric", baseUrl, coords[0], coords[1], apiKey);

        try {
            Map<String, Object> response = restTemplate.getForObject(weatherUrl, Map.class);
            WeatherDto dto = new WeatherDto();
            dto.setDistrict(district);

            if (response != null && response.containsKey("main")) {
                Map<String, Object> main = (Map<String, Object>) response.get("main");
                if (main.containsKey("temp")) dto.setTemperature(Double.parseDouble(main.get("temp").toString()));
                if (main.containsKey("humidity")) dto.setHumidity(Integer.parseInt(main.get("humidity").toString()));
            }

            if (response != null && response.containsKey("weather")) {
                List<Map<String, Object>> weatherList = (List<Map<String, Object>>) response.get("weather");
                if (!weatherList.isEmpty()) {
                    dto.setDescription(weatherList.get(0).get("description").toString());
                }
            }

            // Forecast logic for rain would ideally go by calling /forecast endpoint for rain probability.
            // Placeholder value
            dto.setRainProbability(30);

            return dto;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching weather", e);
        }
    }
}
