package com.foodapp.foodapp_backend.controller;
 
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.RestController;
 
import com.foodapp.foodapp_backend.handler.RiderWebSocketHandler;
 
@RestController

@RequestMapping("/test")

public class OrderSimulationController {
 
    @Autowired
    private RiderWebSocketHandler webSocketHandler;
    
    @PostMapping("/trigger-order")
    public String triggerFakeOrder(@RequestParam(defaultValue = "500.00") double amount) {

        String orderPayload = "{"
                + "\"type\": \"NEW_ORDER\","
                + "\"orderId\": \"ORD-9999\","
                + "\"pickup\": \"Paradise Biryani, Secunderabad\","
                + "\"dropoff\": \"Hitech City, Hyderabad\","
                + "\"amount\": " + amount
                + "}";
        webSocketHandler.broadcastMessage(orderPayload);

        return "Order Notification Sent to Riders!";

    }

}
 