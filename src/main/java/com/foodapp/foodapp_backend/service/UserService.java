package com.foodapp.foodapp_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.dto.UserProfileDTO;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;

	public UserProfileDTO getUserProfile(String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not Found with Email: "+email));
		
		UserProfileDTO userProfileDTO = new UserProfileDTO();
		userProfileDTO.setId(user.getId());
		userProfileDTO.setEmail(email);
		userProfileDTO.setName(user.getName());
		userProfileDTO.setPhone(user.getPhone());
		
		log.info("Getting the User Profile of {} ",user.getName());
		
		return userProfileDTO;
	}

}
