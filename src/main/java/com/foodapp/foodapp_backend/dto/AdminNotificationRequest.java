package com.foodapp.foodapp_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminNotificationRequest {
	
	private String title;
	private String body;

}