package com.foodapp.foodapp_backend.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Component
public class LoginRequest {
	
	private String email;
	private String password;
}
