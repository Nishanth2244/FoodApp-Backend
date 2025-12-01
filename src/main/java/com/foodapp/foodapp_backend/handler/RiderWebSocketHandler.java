package com.foodapp.foodapp_backend.handler;
 
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class RiderWebSocketHandler extends TextWebSocketHandler { 
    // Active Sessions ni store cheskodaniki list

    private static final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Rider connect avvagane list lo add chestam
        sessions.add(session);
        System.out.println("✅ Rider Connected: " + session.getId());
    }
 
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        System.out.println("❌ Rider Disconnected: " + session.getId());
    }
 
    // Ee method ni Controller nundi call chesi Notification pampochu
    public void broadcastMessage(String message) {
        for (WebSocketSession session : sessions) {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
 