package com.foodapp.foodapp_backend.websocket;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.foodapp.foodapp_backend.security.JwtService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
	
	@Autowired
	private JwtService jwtService;

	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Map<String, Object> attributes) {
		
		String query = request.getURI().getQuery();
		
		if(query == null || !query.startsWith("token=")) {
			log.info("token missing");
			return false;
		}
		
		String token = query.substring(6);
		
		try {
			jwtService.extractUsername(token);
			log.info("token validdd");
		}catch (Exception e) {
			log.info("Token is Invald or Expired");
			return false;
		}
		
		String username = jwtService.extractUsername(token);
		attributes.put("username", username);
		
		log.info("Websocket Authenticated User {}", username);
		return true;
	}

	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
			Exception exception) {
		
	}

}
