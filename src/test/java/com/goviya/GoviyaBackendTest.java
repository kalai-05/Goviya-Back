package com.goviya;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.goviya.dto.AuthResponse;
import com.goviya.dto.WeatherDto;
import com.goviya.service.AuthService;
import com.goviya.service.ListingService;
import com.goviya.service.PriceService;
import com.goviya.service.ShopService;
import com.goviya.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class GoviyaBackendTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private ListingService listingService;

    @MockBean
    private PriceService priceService;

    @MockBean
    private WeatherService weatherService;

    @MockBean
    private ShopService shopService;

    @BeforeEach
    public void setupMocks() {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken("mock-token");
        when(authService.verifyOTP(anyString(), anyString())).thenReturn(authResponse);

        when(listingService.getListings(any(), any())).thenReturn(new ArrayList<>());
        when(priceService.getTodayPrices(any())).thenReturn(new ArrayList<>());

        WeatherDto weatherDto = new WeatherDto();
        weatherDto.setTemperature(28.5);
        when(weatherService.getWeather(anyString())).thenReturn(weatherDto);

        when(shopService.getShopsByDistrict(anyString())).thenReturn(new ArrayList<>());
    }

    @Test
    public void testSendOtp() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("phone", "+94771234567");

        mockMvc.perform(post("/api/auth/send-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    public void testGetListings() throws Exception {
        mockMvc.perform(get("/api/listings/")
                .param("district", "Colombo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testGetPrices() throws Exception {
        mockMvc.perform(get("/api/prices/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    public void testGetWeather() throws Exception {
        mockMvc.perform(get("/api/weather/")
                .param("district", "Colombo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.temperature").exists());
    }

    @Test
    public void testGetShops() throws Exception {
        mockMvc.perform(get("/api/shops/")
                .param("district", "Colombo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
