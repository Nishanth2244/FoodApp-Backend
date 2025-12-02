package com.foodapp.foodapp_backend.controller;
 
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.handler.RiderWebSocketHandler;
import com.foodapp.foodapp_backend.repository.UserRepository;
 
@RestController
@RequestMapping("/admin")
public class AdminController {
 
    @Autowired
    private RiderWebSocketHandler riderWebSocketHandler;
    
    @Autowired
    private UserRepository userRepository;
 
    @GetMapping("/active-riders")
    public String getActiveRidersCount() {
        int count = riderWebSocketHandler.getActiveRiderCount();
        return "Total Active Riders: " + count;
    }
    
    @GetMapping("/online-riders")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<User> getOnlineRiders() {
        return userRepository.findByRiderStatus("ONLINE");
    }
}
 