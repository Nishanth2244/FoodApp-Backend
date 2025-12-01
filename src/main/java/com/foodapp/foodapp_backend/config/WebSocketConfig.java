package com.foodapp.foodapp_backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.foodapp.foodapp_backend.handler.RiderWebSocketHandler;

@EnableWebSocket
@Configuration
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer{
	
	@Autowired
	private JwtHandshakeInterceptor jwtHandshakeInterceptor;
	
	@Autowired
	private RiderWebSocketHandler riderWebSocketHandler;
	
	public void registerWebSocketHandlers (WebSocketHandlerRegistry registry) {
		registry.addHandler(riderWebSocketHandler, "/ws/rider")
				.addInterceptors(jwtHandshakeInterceptor)
				.setAllowedOriginPatterns("*");
		
//				.withSockJS();
			}
	
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");       //client sends
		registry.enableSimpleBroker("/topic");  //client subscribes
		
	}

	
}
