package com.foodapp.foodapp_backend.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PushNotificationService {
	
	@Autowired
	private UserRepository userRepository;
    
    // Expo URL
    private static final String EXPO_PUSH_URL = "https://exp.host/--/api/v2/push/send";
    
    private final RestTemplate restTemplate = new RestTemplate();

//    Common method to Send the Notification
    public void sendNotification(String expoPushToken, String title, String body) {
        
        if (expoPushToken == null || expoPushToken.isEmpty()) {
            log.warn("Expo token is null or empty. Cannot send notification.");
            return;
        }

        try {
            // 1. Headers set cheyyali
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // 2. Body create cheyyali (JSON structure)
            String requestBody = new ObjectMapper().writeValueAsString(
                new ExpoPushMessage[]{
                    new ExpoPushMessage(
                        expoPushToken, 
                        title, 
                        body
                    )
                }
            );

            // 3. Request entity create cheyyali
            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            // 4. API call cheyyali
            ResponseEntity<String> response = restTemplate.exchange(
                EXPO_PUSH_URL,
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                 log.info("Notification sent successfully to token: {}. Expo Response: {}", expoPushToken, response.getBody());
            } else {
                 log.error("Failed to send notification. Expo Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            }
            
        } catch (Exception e) {
            log.error("Failed to send push notification to token: {}", expoPushToken, e);
        }
    }
    
    // Inner class (DTO for Expo API Request Body)
    private static class ExpoPushMessage {
        // Expo Push API ki kaavalasina fields
        public String to;
        public String title;
        public String body;
        public String sound = "default"; 

        public ExpoPushMessage(String to, String title, String body) {
            this.to = to;
            this.title = title;
            this.body = body;
        }
    }
    
    public void sendBroadcastNotification(String title, String body) {
        // 1. Database nunchi andhari users tokens ni theesukovali
        List<User> allUsers = userRepository.findAll();
        
        // 2. Valid tokens ni filter chesi, ExpoPushMessage objects list create cheyyali
        List<ExpoPushMessage> messages = allUsers.stream()
            .filter(user -> user.getExpoPushToken() != null && !user.getExpoPushToken().isEmpty())
            // Token Expo token laaga kanipistundaa ani check cheyyadam better, kaani simple ga keep chesthunnaanu
            .map(user -> new ExpoPushMessage(user.getExpoPushToken(), title, body))
            .collect(Collectors.toList());

        if (messages.isEmpty()) {
            log.info("No active Expo tokens found to send broadcast.");
            return;
        }

        try {
            // Expo API batch request ni accept chestundi
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // ObjectMapper use chesi List ni JSON array ga maarchandi
            String requestBody = new ObjectMapper().writeValueAsString(messages); 

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                EXPO_PUSH_URL,
                HttpMethod.POST,
                entity,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                 log.info("Broadcast notification sent successfully to {} users. Expo Response: {}", messages.size(), response.getBody());
            } else {
                 log.error("Failed to send broadcast notification. Expo Status: {}, Body: {}", response.getStatusCode(), response.getBody());
            }
            
        } catch (Exception e) {
            log.error("Failed to send push notification broadcast", e);
        }
    }
}