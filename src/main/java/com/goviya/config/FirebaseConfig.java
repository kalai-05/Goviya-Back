package com.goviya.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials:firebase-credentials.json}")
    private String credentialsPath;

    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream(credentialsPath);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase application has been initialized");
            }
        } catch (FileNotFoundException e) {
            log.warn("Firebase credentials file not found at path: {}. Firebase will not be initialized.", credentialsPath);
        } catch (IOException e) {
            log.error("Error reading Firebase credentials file", e);
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() {
        if (FirebaseApp.getApps().isEmpty()) {
            return null;
        }
        return FirebaseMessaging.getInstance();
    }

    @Bean
    public com.google.firebase.auth.FirebaseAuth firebaseAuth() {
        if (FirebaseApp.getApps().isEmpty()) {
            return null;
        }
        return com.google.firebase.auth.FirebaseAuth.getInstance();
    }
}
