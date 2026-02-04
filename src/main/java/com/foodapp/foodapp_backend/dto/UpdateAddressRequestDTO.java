package com.foodapp.foodapp_backend.dto;

import lombok.Data;

@Data
public class UpdateAddressRequestDTO {
	
		private String recipentName;
		private String street;
		private String city;
		private String state;
		private String zipcode;
		private String country;
}
