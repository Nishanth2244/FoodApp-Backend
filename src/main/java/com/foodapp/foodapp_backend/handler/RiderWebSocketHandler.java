package com.foodapp.foodapp_backend.handler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    // Maps
    private static final Map<String, WebSocketSession> activeRiders = new ConcurrentHashMap<>();
    private static final Map<String, WebSocketSession> adminSessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    @Lazy
    private UserService userService;
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        // Separate Admins and Riders
        if (username != null) {
             // Simple role check based on your logic (can be improved with authorities)
             if(username.toLowerCase().contains("admin")) {
                 adminSessions.put(username, session);
                 System.out.println("👨‍💻 Admin Connected: " + username);
             } else {
                 System.out.println("🔗 Rider Connected: " + username);
             }
        }
    }
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String username = (String) session.getAttributes().get("username");
        try {
            Map<String, String> data = objectMapper.readValue(payload, Map.class);
            if ("STATUS_UPDATE".equals(data.get("type"))) {
                String status = data.get("status");
                if ("ONLINE".equals(status)) {
                    activeRiders.put(username, session);
                    userService.updateRiderStatus(username, "ONLINE");
                } else if ("OFFLINE".equals(status)) {
                    activeRiders.remove(username);
                    userService.updateRiderStatus(username, "OFFLINE");
                }
                // Notify Admins about count change
                broadcastToAdmins("{\"type\": \"RIDER_COUNT_UPDATE\", \"count\": " + activeRiders.size() + "}");
            }
        } catch (Exception e) {
            System.err.println("Error parsing WS message: " + e.getMessage());
        }
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if (username != null) {
            activeRiders.remove(username);
            adminSessions.remove(username);
            try {
                 if(!username.toLowerCase().contains("admin")) {
                     userService.updateRiderStatus(username, "OFFLINE");
                 }
            } catch (Exception e) {}
        }
    }
 
    // ✅ Broadcast to all Admins (For New Orders & Counts)
    public void broadcastToAdmins(String message) {
        for (WebSocketSession session : adminSessions.values()) {
            try {
                if (session.isOpen()) session.sendMessage(new TextMessage(message));
            } catch (IOException e) { e.printStackTrace(); }
        }
    }
    // ✅ Send Message to Specific Rider (Order Assignment)
    public boolean sendToRider(String riderEmail, String message) {
        WebSocketSession session = activeRiders.get(riderEmail);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                System.out.println("🚀 Sent to Rider (" + riderEmail + "): " + message);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("⚠️ Rider not found in active sessions: " + riderEmail);
        }
        return false;
    }
    
    
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