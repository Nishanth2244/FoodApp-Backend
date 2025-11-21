 package com.foodapp.foodapp_backend.service;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.dto.LoginRequest;
import com.foodapp.foodapp_backend.dto.UserProfileDTO;
import com.foodapp.foodapp_backend.dto.UserProfileUpdateRequest;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LoginRequest loginRequest;
	
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

	public User updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest, String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Email is not exist"));
		
		user.setName(userProfileUpdateRequest.getName());
		log.info("Succesfully Name changed from {} to {}",user.getName(), userProfileUpdateRequest.getName());
		return userRepository.save(user);
	}


}
