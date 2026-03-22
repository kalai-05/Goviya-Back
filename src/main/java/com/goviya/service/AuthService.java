package com.goviya.service;

import com.goviya.dto.AuthResponse;
import com.goviya.dto.UserDto;
import com.goviya.model.User;
import com.goviya.repository.UserRepository;
import com.goviya.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Random;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final com.google.firebase.auth.FirebaseAuth firebaseAuth;
    private final Map<String, String> otpStorage = new ConcurrentHashMap<>();

    public void sendOTP(String phone) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put(phone, otp);
        
        // Log OTP in dev mode
        log.info("DEV MODE - OTP for {}: {}", phone, otp);
    }

    public AuthResponse verifyOTP(String phone, String otp) {
        String storedOtp = otpStorage.get(phone);
        
        if (storedOtp == null) {
            throw new RuntimeException("OTP expired");
        }
        if (!storedOtp.equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }
        otpStorage.remove(phone);
        
        User user = userRepository.findByPhone(phone)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setPhone(phone);
                    return userRepository.save(newUser);
                });
                
        String token = jwtUtil.generateToken(user.getId(), user.getRole() != null ? user.getRole() : "BUYER");
        return new AuthResponse(token, mapToUserDto(user));
    }

    public AuthResponse loginWithFirebase(String idToken) {
        try {
            com.google.firebase.auth.FirebaseToken decodedToken = firebaseAuth.verifyIdToken(idToken);
            String phone = (String) decodedToken.getClaims().get("phone_number");
            
            User user = userRepository.findByPhone(phone)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setPhone(phone);
                        return userRepository.save(newUser);
                    });

            String token = jwtUtil.generateToken(user.getId(), user.getRole() != null ? user.getRole() : "BUYER");
            return new AuthResponse(token, mapToUserDto(user));
        } catch (Exception e) {
            log.error("Firebase token verification failed", e);
            throw new RuntimeException("Invalid Firebase token");
        }
    }

    public UserDto registerProfile(String userId, String name, String role, String district, String language) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setName(name);
        user.setRole(role);
        user.setDistrict(district);
        user.setLanguage(language);
        return mapToUserDto(userRepository.save(user));
    }

    public UserDto getCurrentUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mapToUserDto(user);
    }

    private UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setPhone(user.getPhone());
        dto.setName(user.getName());
        dto.setRole(user.getRole());
        dto.setDistrict(user.getDistrict());
        dto.setLanguage(user.getLanguage());
        dto.setFcmToken(user.getFcmToken());
        dto.setRating(user.getRating());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
