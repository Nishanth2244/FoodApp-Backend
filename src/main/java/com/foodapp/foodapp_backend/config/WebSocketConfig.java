package com.foodapp.foodapp_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import com.foodapp.foodapp_backend.handler.RiderWebSocketHandler;
import com.foodapp.foodapp_backend.handler.UserWebSocketHandler; // ✅ Import

@Configuration
@EnableWebSocket 
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private RiderWebSocketHandler riderWebSocketHandler;

    @Autowired
    private UserWebSocketHandler userWebSocketHandler; // ✅ Inject

    @Autowired
    private JwtHandshakeInterceptor jwtHandshakeInterceptor;
 
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

    	// Rider Endpoint
        registry.addHandler(riderWebSocketHandler, "/ws/rider")
                .addInterceptors(jwtHandshakeInterceptor) 
                .setAllowedOrigins("*");

        // ✅ User Endpoint Added
        registry.addHandler(userWebSocketHandler, "/ws/user")
                .addInterceptors(jwtHandshakeInterceptor) 
                .setAllowedOrigins("*"); 
    }
}
 