package com.foodapp.foodapp_backend.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.dto.UserProfileDTO;
import com.foodapp.foodapp_backend.dto.UserProfileUpdateRequest;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/myProfile")
	public UserProfileDTO getProfile(Principal principal) {
		
		String email = principal.getName();
		return userService.getUserProfile(email);
	}
	
	@PutMapping("/updateProfile")
	public User updateProfile(@RequestBody UserProfileUpdateRequest userProfileUpdateRequest, Principal principal) {
		
		String email = principal.getName();
		return userService.updateUserProfile(userProfileUpdateRequest, email);
	}

}
