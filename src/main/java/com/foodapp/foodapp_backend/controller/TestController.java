package com.foodapp.foodapp_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
	
    @GetMapping("/secure-check")
    public String secureCheck() {
        return "JWT Working ✅";
    }

}
