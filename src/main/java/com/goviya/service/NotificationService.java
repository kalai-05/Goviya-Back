package com.goviya.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.goviya.model.User;
import com.goviya.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    @org.springframework.beans.factory.annotation.Autowired(required = false)
    private FirebaseMessaging firebaseMessaging;
    
    private final UserRepository userRepository;

    public void sendToUser(String userId, String title, String body) {
        userRepository.findById(userId).ifPresent(user -> {
            if (user.getFcmToken() == null || user.getFcmToken().isEmpty()) {
                log.warn("Cannot send notification. FCM token is null for user {}", userId);
                return;
            }

            if (firebaseMessaging == null) {
                log.warn("Firebase Messaging not initialized");
                return;
            }

            Message message = Message.builder()
                    .setToken(user.getFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();
            try {
                String response = firebaseMessaging.send(message);
                log.info("Successfully sent message: {}", response);
            } catch (Exception e) {
                log.error("Failed to send FCM message", e);
            }
        });
    }

    public void sendToRole(String role, String title, String body) {
        List<String> tokens = userRepository.findByRole(role).stream()
                .filter(u -> u.getFcmToken() != null && !u.getFcmToken().isEmpty())
                .map(User::getFcmToken)
                .collect(Collectors.toList());

        if (tokens.isEmpty()) return;

        if (firebaseMessaging == null) {
            log.warn("Firebase Messaging not initialized");
            return;
        }

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();
        try {
            firebaseMessaging.sendMulticast(message);
            log.info("Successfully sent multicast message");
        } catch (Exception e) {
            log.error("Failed to send multicast message", e);
        }
    }

    public void notifyFarmersInDistrict(String district, String cropName) {
        notifyFarmersInDistrict(district, "New buyer request!", "A buyer needs " + cropName + " in " + district);
    }

    public void notifyFarmersInDistrict(String district, String title, String body) {
        List<String> tokens = userRepository.findByRoleAndDistrict("FARMER", district).stream()
                .filter(u -> u.getFcmToken() != null && !u.getFcmToken().isEmpty())
                .map(User::getFcmToken)
                .collect(Collectors.toList());

        if (tokens.isEmpty() || firebaseMessaging == null) return;

        MulticastMessage message = MulticastMessage.builder()
                .addAllTokens(tokens)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        try {
            firebaseMessaging.sendMulticast(message);
        } catch (Exception e) {
            log.error("Failed to send district notification", e);
        }
    }

    public void notifyPriceAlert(String cropName, Double changePercent) {
        sendToRole("FARMER", "Price alert! " + cropName,
            cropName + " price up " + changePercent + "% - good time to sell!");
    }
}
