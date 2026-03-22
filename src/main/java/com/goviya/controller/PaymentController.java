package com.goviya.controller;

import com.goviya.dto.PaymentInitiateResponse;
import com.goviya.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    private String getCurrentUserId(Authentication authentication) {
        return authentication.getName();
    }

    @PostMapping("/initiate")
    public PaymentInitiateResponse initiatePayment(@RequestBody Map<String, Object> request, Authentication authentication) {
        String orderId = (String) request.get("orderId");
        // Safe cast mapping based on expected JSON input structure mapped into String generics
        @SuppressWarnings("unchecked")
        Map<String, String> buyerDetails = (Map<String, String>) request.get("buyerDetails");
        return paymentService.initiatePayment(orderId, buyerDetails);
    }

    @PostMapping("/notify")
    public String handleNotify(@RequestParam Map<String, String> params) {
        paymentService.handlePaymentNotification(params);
        return "OK";
    }
}
