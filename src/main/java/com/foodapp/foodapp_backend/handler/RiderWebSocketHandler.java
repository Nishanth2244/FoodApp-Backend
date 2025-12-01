package com.foodapp.foodapp_backend.handler;
 
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
@Component
public class RiderWebSocketHandler extends TextWebSocketHandler {
 
    // Active Riders ni store cheskodaniki Map (Email -> Session)
    // ConcurrentHashMap thread-safe ga untundi
    private static final Map<String, WebSocketSession> activeRiders = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
 
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        // Connection open avvagane inka 'online' kadu, just connected.
        System.out.println("🔗 Rider Connected (Waiting for status): " + username);
    }
 
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String username = (String) session.getAttributes().get("username");
 
        // Frontend nundi vachina JSON ni parse cheyyali
        // Ex: {"type": "STATUS_UPDATE", "status": "ONLINE"}
        Map<String, String> data = objectMapper.readValue(payload, Map.class);
 
        if ("STATUS_UPDATE".equals(data.get("type"))) {
            String status = data.get("status");
 
            if ("ONLINE".equals(status)) {
                activeRiders.put(username, session);
                System.out.println("✅ Rider is NOW ONLINE: " + username);
                System.out.println("📊 Total Active Riders: " + activeRiders.size());
            } else if ("OFFLINE".equals(status)) {
                activeRiders.remove(username);
                System.out.println("⛔ Rider went OFFLINE: " + username);
                System.out.println("📊 Total Active Riders: " + activeRiders.size());
            }
            // Optional: Admin ki update pampochu ikkade
            // adminWebSocketHandler.broadcast("ACTIVE_RIDERS_COUNT", activeRiders.size());
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        activeRiders.remove(username); // Disconnect ayithe automatic ga remove chestam
        System.out.println("❌ Rider Disconnected & Removed: " + username);
    }
    // Riders ki message pampadaniki (Only Online Riders)
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
    // Admin ki count ivvadaniki helper method
    public int getActiveRiderCount() {
        return activeRiders.size();
    }
}