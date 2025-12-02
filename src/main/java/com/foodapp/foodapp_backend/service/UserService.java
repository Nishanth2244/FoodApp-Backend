 package com.foodapp.foodapp_backend.service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.dto.LoginRequest;
import com.foodapp.foodapp_backend.dto.RiderStatusUpdateDTO;
import com.foodapp.foodapp_backend.dto.UserProfileDTO;
import com.foodapp.foodapp_backend.dto.UserProfileUpdateRequest;
import com.foodapp.foodapp_backend.entity.Role;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.handler.RiderWebSocketHandler;
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
	
    @Autowired
    @Lazy
    private RiderWebSocketHandler riderWebSocketHandler;
	
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
                .filter(user -> !user.getEmail().equals(adminEmail))
                .filter(user -> !user.getRoles().contains(Role.ROLE_RIDER))
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

	public User updateRiderStatus(String email, String status) {
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not found with: "+ email));
		// 1. Update Database
		user.setRiderStatus(status);
		User savedUser = userRepository.save(user);
		log.info("{} rider is going to {}", email, status);
		
		// 2. ✅ Get Real-time Count (Can be from DB or Memory)
        try {
            // Here we can get accurate count from DB or Handler memory
            int activeCount = riderWebSocketHandler.getActiveRiderCount(); 
            // 3. ✅ Broadcast to Admin via WebSocket (The "Topic")
            String jsonMessage = "{\"type\": \"RIDER_COUNT_UPDATE\", \"count\": " + activeCount + "}";
            riderWebSocketHandler.broadcastToAdmins(jsonMessage);
        } catch (Exception e) {
            log.error("Failed to broadcast rider status update via WebSocket", e);
        }
		return savedUser;
	}
	
	

	public RiderStatusUpdateDTO getRiderStatus(String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not found with name: "+ email));
		
		RiderStatusUpdateDTO riderStatusUpdateDTO = new RiderStatusUpdateDTO();
		riderStatusUpdateDTO.setStatus(user.getRiderStatus());
		log.info("getting {} rider status {}", email, user.getRiderStatus());

		return riderStatusUpdateDTO;
	}


}
