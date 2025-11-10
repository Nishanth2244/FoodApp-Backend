package com.foodapp.foodapp_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginRequest {
	
	private String email;
	private String password;
}
