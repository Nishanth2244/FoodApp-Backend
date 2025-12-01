package com.foodapp.foodapp_backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.dto.AdminNotificationRequest;
import com.foodapp.foodapp_backend.dto.ExpoTokenRequestDTO;
import com.foodapp.foodapp_backend.dto.RiderStatusUpdateDTO;
import com.foodapp.foodapp_backend.dto.UserProfileDTO;
import com.foodapp.foodapp_backend.dto.UserProfileUpdateRequest;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.service.PushNotificationService;
import com.foodapp.foodapp_backend.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PushNotificationService pushNotificationService;
	
	@GetMapping("/myProfile")
	public UserProfileDTO getProfile(Principal principal) {
		
		String email = principal.getName();
		return userService.getUserProfile(email);
	}
	
	@PutMapping("/updateProfile")
	public User updateProfile(@RequestBody UserProfileUpdateRequest userProfileUpdateRequest, Principal principal) {
		
		String email = principal.getName();
		log.info("Request came to change name of {}",email);
		return userService.updateUserProfile(userProfileUpdateRequest, email);
	}
	
	@GetMapping("/allUsers")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<UserProfileDTO> getUsers(Principal principal){
		
		String adminEmail = principal.getName();
		return userService.getAllUsers(adminEmail);
	}
	
	
	@PutMapping("/register-expo-token")
	public String registerExpoToken(@RequestBody ExpoTokenRequestDTO expoTokenRequestDTO, Principal principal) {
		
		if(expoTokenRequestDTO.getExpoToken() == null || expoTokenRequestDTO.getExpoToken().isEmpty()) {
			log.info("The expo token is Empty {} ", expoTokenRequestDTO.getExpoToken());
			return "The Expo Token is Empty";
		}
		
		String email = principal.getName();
		
		userService.saveExpoToken(email,expoTokenRequestDTO.getExpoToken());
		return "Token saved Succesfully in DB"+expoTokenRequestDTO.getExpoToken();
	}
	
	@PostMapping("/broadcast-not")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String broadCastnot(@RequestBody AdminNotificationRequest adminNotificationRequest) {
		
		log.info("ADMIN request to sent notification");
		
		new Thread(() -> {
			pushNotificationService.sendBroadcastNotification(adminNotificationRequest.getTitle(), adminNotificationRequest.getBody());
		}).start();
		
		return "Notification sent to all Users";
	}
	
	
	@PostMapping("/status-update")
	@PreAuthorize("hasRole('ROLE_RIDER')")
	public User statusUpdate(@RequestBody RiderStatusUpdateDTO riderStatusUpdateDTO, Principal principal) {
		
		String email = principal.getName();
		String status = riderStatusUpdateDTO.getStatus();
		return userService.updateRiderStatus(email,status);
	}
	
	
	@GetMapping("RiderStatus")
	@PreAuthorize("hasRole('ROLE_RIDER')")
	public RiderStatusUpdateDTO getStatus(Principal principal) {
		
		String email = principal.getName();
		return userService.getRiderStatus(email);
	}
}
