//package com.foodapp.foodapp_backend.websocket;
// 
//import java.util.Map;
// 
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.server.ServerHttpRequest;
//import org.springframework.http.server.ServerHttpResponse;
//import org.springframework.http.server.ServletServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.web.socket.WebSocketHandler;
//import org.springframework.web.socket.server.HandshakeInterceptor;
// 
//import com.foodapp.foodapp_backend.security.JwtService;
// 
//import lombok.extern.slf4j.Slf4j;
// 
//@Slf4j
//@Component
//public class JwtHandshakeInterceptor implements HandshakeInterceptor {
// 
//    @Autowired
//    private JwtService jwtService;
// 
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
// 
//        if (request instanceof ServletServerHttpRequest) {
//            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
// 
//            // 🔥 Read token from headers
//            String authHeader = servletRequest.getServletRequest().getHeader("Authorization");
//            
//            log.info("token from the header {}", authHeader);
// 
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                log.info("Token missing in headers");
//                return false; // reject handshake
//            }
// 
//            String token = authHeader.substring(7); // remove "Bearer "
//            try {
//                jwtService.extractUsername(token);
//                log.info("Token valid");
//            } catch (Exception e) {
//                log.info("Token is invalid or expired");
//                return false;
//            }
// 
//            String username = jwtService.extractUsername(token);
//            attributes.put("username", username);
//            log.info("WebSocket Authenticated User {}", username);
//        }
// 
//        return true;
//    }
// 
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
//                               WebSocketHandler wsHandler, Exception exception) {
//        // do nothing
//    }
//}