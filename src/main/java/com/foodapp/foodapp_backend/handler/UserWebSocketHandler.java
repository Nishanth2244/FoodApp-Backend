package com.foodapp.foodapp_backend.handler;
 
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
 
@Component
public class UserWebSocketHandler extends TextWebSocketHandler {
 
    // User Email -> Session Mapping
    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();
 
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if(username != null) {
            userSessions.put(username, session);
            System.out.println("👤 User Connected: " + username);
        }
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = (String) session.getAttributes().get("username");
        if(username != null) {
            userSessions.remove(username);
            System.out.println("❌ User Disconnected: " + username);
        }
    }
 

    public void sendNotificationToUser(String email, String message) {
        WebSocketSession session = userSessions.get(email);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
                System.out.println("📩 Sent to " + email + ": " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}