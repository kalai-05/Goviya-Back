package com.goviya.controller;

import com.goviya.dto.ApiResponse;
import com.goviya.dto.SendOtpRequest;
import com.goviya.dto.VerifyOtpRequest;
import com.goviya.dto.RegisterRequest;
import com.goviya.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    
    private final AuthService authService;

    private String getCurrentUserId(Authentication authentication) {
        return authentication.getName();
    }

    @PostMapping("/send-otp")
    public ApiResponse<?> sendOtp(@Valid @RequestBody SendOtpRequest request) {
        authService.sendOTP(request.getPhone());
        return ApiResponse.success("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ApiResponse<?> verifyOtp(@Valid @RequestBody VerifyOtpRequest request) {
        return ApiResponse.success(authService.verifyOTP(request.getPhone(), request.getOtp()));
    }

    @PostMapping("/firebase-login")
    public ApiResponse<?> firebaseLogin(@Valid @RequestBody com.goviya.dto.FirebaseLoginRequest request) {
        return ApiResponse.success(authService.loginWithFirebase(request.getIdToken()));
    }

    @PostMapping("/register")
    public ApiResponse<?> register(@Valid @RequestBody RegisterRequest request, Authentication authentication) {
        String userId = getCurrentUserId(authentication);
        return ApiResponse.success(authService.registerProfile(userId, request.getName(), request.getRole(), request.getDistrict(), request.getLanguage()));
    }

    @GetMapping("/me")
    public ApiResponse<?> getCurrentUser(Authentication authentication) {
        String userId = getCurrentUserId(authentication);
        return ApiResponse.success(authService.getCurrentUser(userId));
    }
}
