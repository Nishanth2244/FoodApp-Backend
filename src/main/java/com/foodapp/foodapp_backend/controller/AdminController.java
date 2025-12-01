package com.foodapp.foodapp_backend.controller;
 
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.handler.RiderWebSocketHandler;
 
@RestController
@RequestMapping("/admin")
public class AdminController {
 
    @Autowired
    private RiderWebSocketHandler riderWebSocketHandler;
 
    @GetMapping("/active-riders")
    public String getActiveRidersCount() {
        int count = riderWebSocketHandler.getActiveRiderCount();
        return "Total Active Riders: " + count;
    }
}
 