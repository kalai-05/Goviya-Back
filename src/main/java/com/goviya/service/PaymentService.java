package com.goviya.service;

import com.goviya.dto.PaymentInitiateResponse;
import com.goviya.model.Order;
import com.goviya.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;
    private final NotificationService notificationService;

    @Value("${payhere.merchant-id}")
    private String merchantId;

    @Value("${payhere.secret}")
    private String payhereSecret;

    public PaymentInitiateResponse initiatePayment(String orderId, Map<String, String> buyerDetails) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new RuntimeException("Order not found"));

        PaymentInitiateResponse response = new PaymentInitiateResponse();
        response.setMerchantId(merchantId);
        response.setOrderId(orderId);
        response.setAmount(order.getTotalPrice());
        response.setCurrency("LKR");
        response.setOrderDescription("Payment for order " + orderId);

        response.setFirstName(buyerDetails.getOrDefault("firstName", "John"));
        response.setLastName(buyerDetails.getOrDefault("lastName", "Doe"));
        response.setEmail(buyerDetails.getOrDefault("email", "john@example.com"));
        response.setPhone(buyerDetails.getOrDefault("phone", "0770000000"));

        String amountFormatted = String.format("%.2f", order.getTotalPrice());
        String hash = generatePayhereHash(merchantId, orderId.toString(), amountFormatted, "LKR", payhereSecret);
        response.setHash(hash);

        return response;
    }

    public void handlePaymentNotification(Map<String, String> params) {
        String merchantIdParam = params.get("merchant_id");
        String orderIdRef = params.get("order_id");
        String payhereAmount = params.get("payhere_amount");
        String payhereCurrency = params.get("payhere_currency");
        String statusCode = params.get("status_code");
        String md5sig = params.get("md5sig");

        String generatedHash = generatePayhereHash(merchantIdParam, orderIdRef, payhereAmount, payhereCurrency, payhereSecret);

        if (generatedHash.equalsIgnoreCase(md5sig) && "2".equals(statusCode)) {
            Order order = orderRepository.findById(orderIdRef).orElseThrow();
            order.setPaymentStatus("PAID");
            orderRepository.save(order);

            orderService.confirmOrder(order.getId(), order.getFarmerId());
            notificationService.sendToUser(order.getFarmerId(), "Payment Received", "Payment for order " + orderIdRef + " has been received!");
        } else {
            throw new RuntimeException("Hash mismatch or payment failed");
        }
    }

    private String generatePayhereHash(String merchant, String order, String amount, String currency, String secret) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] secretDigest = md.digest(secret.getBytes());
            String secretHash = String.format("%032x", new BigInteger(1, secretDigest)).toUpperCase();

            String data = merchant + order + amount + currency + secretHash;
            byte[] dataDigest = md.digest(data.getBytes());
            return String.format("%032x", new BigInteger(1, dataDigest)).toUpperCase();
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }
}
