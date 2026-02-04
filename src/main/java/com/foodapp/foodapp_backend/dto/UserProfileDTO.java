package com.foodapp.foodapp_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserProfileDTO {
	
	private Long id;
	private String name;
	private String email;
	private String phone;

}
