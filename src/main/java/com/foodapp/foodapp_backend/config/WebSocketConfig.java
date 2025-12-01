package com.foodapp.foodapp_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@EnableWebSocket
@Configuration
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer{
	
	public  void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/ws/rider")
				.setAllowedOriginPatterns("*");
//				.withSockJS();
			}
	
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.setApplicationDestinationPrefixes("/app");       //client sends
		registry.enableSimpleBroker("/topic");  //client subscribes
	}

	
}
