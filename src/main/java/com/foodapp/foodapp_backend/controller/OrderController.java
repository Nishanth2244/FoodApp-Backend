package com.foodapp.foodapp_backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.entity.Order;
import com.foodapp.foodapp_backend.service.OrderService;

@RestController
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@PostMapping("/placeOrder")
	public Order placeOrder(Principal principal) {
		
		String email = principal.getName();
		return orderService.placeOrder(email);
	}
	
	@GetMapping("/myHistory")
	public List<Order> getOrdersHistory(Principal principal){
		
		String email = principal.getName();
		return orderService.myOrders(email);
	}
}
