package com.foodapp.foodapp_backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.dto.OrderUpdateStatusDTO;
import com.foodapp.foodapp_backend.entity.Order;
import com.foodapp.foodapp_backend.repository.OrderRepository;
import com.foodapp.foodapp_backend.service.OrderService;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/order")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private OrderRepository orderRepository;
	
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
	
	
	@PutMapping("updateStatus/{orderId}")
	@PreAuthorize("hasRole('ROLE_ADMIN', 'ROLE_RIDER)")
	public Order updateOrderStatus(@PathVariable Long orderId, @RequestBody OrderUpdateStatusDTO orderUpdateStatusDTO) {
		
		log.info("ADMIN: updting order staus for order: {}",orderId);
		return orderService.updateStatus(orderId,orderUpdateStatusDTO);
	}

	
	@GetMapping("allOrders")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<Order> getAllOrder() {
		return orderService.allOrders();
	}
	
	
	@PutMapping("cancel/{orderId}")
	public Order cancelOrder(@PathVariable Long orderId, Principal principal){
		
		String email = principal.getName();
		return orderService.cancelOrder(orderId,email);
		
	}
	
    @PutMapping("/assign/{orderId}/{riderId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Order assignOrder(@PathVariable Long orderId, @PathVariable Long riderId) {
        log.info("Request to assign Order {} to Rider {}", orderId, riderId);
        return orderService.assignRider(orderId, riderId);
    }
    
    
    @GetMapping("/{orderId}")
    public Order getOrderById(@PathVariable Long orderId) {
    	
    	return orderService.getOrder(orderId);
    }
}
