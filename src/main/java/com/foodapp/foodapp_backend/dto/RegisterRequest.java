package com.foodapp.foodapp_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RegisterRequest {
	
    private String name;
    private String email;
    private String phone;
    private String password;
}
