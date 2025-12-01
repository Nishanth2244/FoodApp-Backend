package com.foodapp.foodapp_backend.handler;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodapp.foodapp_backend.service.UserService;
 
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
@Component
public class RiderWebSocketHandler extends TextWebSocketHandler {
 
    // Active Riders Memory Map (For Admin Count)
    private static final Map<String, WebSocketSession> activeRiders = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
 
    // ✅ DB Update kosam Service Inject chesthunnam
    @Autowired
    private UserService userService;
 
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        System.out.println("🔗 Rider Connected: " + username);
    }
 
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String username = (String) session.getAttributes().get("username");
 
        // JSON Parsing
        Map<String, String> data = objectMapper.readValue(payload, Map.class);
 
        if ("STATUS_UPDATE".equals(data.get("type"))) {
            String status = data.get("status");
 
            if ("ONLINE".equals(status)) {
                // 1. Memory lo Update (Admin Live Count kosam)
                activeRiders.put(username, session);
                // 2. Database lo Update (Permanent Storage kosam)
                try {
                    userService.updateRiderStatus(username, "ONLINE");
                    System.out.println("✅ Rider ONLINE (DB Updated): " + username);
                } catch (Exception e) {
                    System.err.println("❌ Failed to update DB for ONLINE: " + e.getMessage());
                }
 
            } else if ("OFFLINE".equals(status)) {
                // 1. Memory nundi Remove
                activeRiders.remove(username);
                // 2. Database lo Update
                try {
                    userService.updateRiderStatus(username, "OFFLINE");
                    System.out.println("⛔ Rider OFFLINE (DB Updated): " + username);
                } catch (Exception e) {
                    System.err.println("❌ Failed to update DB for OFFLINE: " + e.getMessage());
                }
            }
            // Optional: Print active count
            System.out.println("📊 Current Active Riders in Memory: " + activeRiders.size());
        }
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
 
        if (username != null) {
            activeRiders.remove(username);
            // ✅ Connection cut ayithe automatic ga OFFLINE cheseddam
            try {
                userService.updateRiderStatus(username, "OFFLINE");
                System.out.println("❌ Rider Disconnected & Set to OFFLINE: " + username);
            } catch (Exception e) {
                System.err.println("Error updating status on close: " + e.getMessage());
            }
        }
    }
 
    // Helper methods...
    public void broadcastMessage(String message) {
        for (WebSocketSession session : activeRiders.values()) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    public int getActiveRiderCount() {
        return activeRiders.size();
    }
}