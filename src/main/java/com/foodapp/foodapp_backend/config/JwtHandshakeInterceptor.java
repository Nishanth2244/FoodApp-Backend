package com.foodapp.foodapp_backend.config;
 
import org.springframework.http.server.ServerHttpRequest;

import org.springframework.http.server.ServerHttpResponse;

import org.springframework.http.server.ServletServerHttpRequest;

import org.springframework.web.socket.WebSocketHandler;

import org.springframework.web.socket.server.HandshakeInterceptor;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import com.foodapp.foodapp_backend.security.JwtService;
 
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    
	@Autowired
    private JwtService jwtService;
 
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
    		WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();

            // Extract Token from URL
            String query = servletRequest.getQueryString();

            String token = null;
 
            if (query != null && query.contains("token=")) {
                for (String param : query.split("&")) {
                    if (param.startsWith("token=")) {
                        token = param.substring(6);
                        break;
                    }
                }
            }
 
            // 2. validate Token
            if (token != null) {
                try {
                    String username = jwtService.extractUsername(token);
                    if (jwtService.isTokenValid(token, username)) {
                        // Valid User store in Session Attributes
                        attributes.put("username", username);
                        System.out.println("✅ WebSocket Auth Success: " + username);
                        return true; // Connection Accept
                    }
                } catch (Exception e) {
                    System.out.println("❌ WebSocket Auth Failed: Invalid Token");
                }
            }
        }
        System.out.println("❌ WebSocket Handshake Rejected: No Token found");
        return false; // Connection Reject
    }
 
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, 
                              WebSocketHandler wsHandler, Exception exception) {
    }
}
 