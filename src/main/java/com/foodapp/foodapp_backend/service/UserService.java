 package com.foodapp.foodapp_backend.service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.dto.LoginRequest;
import com.foodapp.foodapp_backend.dto.RiderStatusDTO;
import com.foodapp.foodapp_backend.dto.UserProfileDTO;
import com.foodapp.foodapp_backend.dto.UserProfileUpdateRequest;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LoginRequest loginRequest;
	
	@Autowired
	private PushNotificationService pushNotificationService;
	
	@Cacheable(value = "UserProfile", key = "#email")
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

	@CacheEvict(value = "UserProfile", key = "#email")
	public User updateUserProfile(UserProfileUpdateRequest userProfileUpdateRequest, String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Email is not exist"));
		
		String oldName = user.getName();
		
		user.setName(userProfileUpdateRequest.getName());
		
		User updatedUser = userRepository.save(user);
		
		log.info("Succesfully Name changed from {} to {}",oldName, updatedUser.getName());
		
		if(updatedUser.getExpoPushToken() != null && !updatedUser.getExpoPushToken().isEmpty()) {
			
			String title = "Profile Updated";
			
			String body = "Profile Updated from "+oldName+" to "+ updatedUser.getName();
			
			new Thread(() -> {
				pushNotificationService.sendNotification(updatedUser.getExpoPushToken(), title, body);
			}).start();
			log.info("Profile Update Notification sent");
		}
		return updatedUser;
	}

	public List<UserProfileDTO> getAllUsers(String adminEmail) {
		
		List<User> allUsers = userRepository.findAll();
		
		return allUsers.stream()
                .filter(user -> !user.getEmail().equals(adminEmail)) // <-- EE LINE ADMIN NI FILTER CHESTUNDI
                .map(user -> {
                    UserProfileDTO profileDTO = new UserProfileDTO();
                    profileDTO.setId(user.getId());
                    profileDTO.setName(user.getName());
                    profileDTO.setEmail(user.getEmail());
                    profileDTO.setPhone(user.getPhone());
                    return profileDTO;
                }).collect(Collectors.toList());
	}

	@Transactional
	public User saveExpoToken(String email, String expoToken) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with email :"+ email));
		
		user.setExpoPushToken(expoToken);
		log.info("Expo Token succesfully save in the DB of {} {} ", email, expoToken);
		return userRepository.save(user);
		
	}

	public User updateStatus(String email, String status) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with Id: "+email));
		
		user.setRiderStatus(status);
		
		log.info("Rider status updated to : {}", status);
		return userRepository.save(user);
	}

	public RiderStatusDTO getStatus(String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not found with email: "+ email));
		
		RiderStatusDTO riderStatusDTO = new RiderStatusDTO();
		riderStatusDTO.setRiderStatus(user.getRiderStatus());
		           
		log.info("Fetching Rider status : {}", user.getRiderStatus());
		return riderStatusDTO;
	}


}
