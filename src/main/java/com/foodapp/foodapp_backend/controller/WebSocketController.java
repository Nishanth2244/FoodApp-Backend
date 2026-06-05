//package com.foodapp.foodapp_backend.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Controller;
//
//import com.foodapp.foodapp_backend.dto.Message;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Controller
//public class WebSocketController {
//	
//	@Autowired
//	private SimpMessagingTemplate simpMessagingTemplate;
//	
//	@MessageMapping("/send")
//	private void sendMessage(Message msg, SimpMessageHeaderAccessor headers) {
//		
//		String sender = (String) headers.getSessionAttributes().get("username");
//		
//		log.info("sender is {}", sender);
//		msg.setFrom(sender);
//		
//		simpMessagingTemplate.convertAndSendToUser(msg.getTo(), "/queue/message", msg);
//		String topic = "/queue/message";
//		log.info("sending message to {} topic is {}", msg.getTo(), topic);
//		
//		
//	}
//}
