package com.foodapp.foodapp_backend.service;

import java.lang.System.Logger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.entity.Cart;
import com.foodapp.foodapp_backend.entity.CartItem;
import com.foodapp.foodapp_backend.entity.Order;
import com.foodapp.foodapp_backend.entity.OrderItem;
import com.foodapp.foodapp_backend.repository.CartRepository;
import com.foodapp.foodapp_backend.repository.OrderRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OrderService {
	
	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	public Order placeOrder(String email) {
		
		Cart cart = cartService.myCartitems(email);
		
		if(cart.getItems().isEmpty()) {
			throw new RuntimeException("Your cart is Empty: "+cart.getItems());
		}
		
		Order order = new Order();
		order.setUser(cart.getUser());
		order.setTotalAmount(cart.getTotalAmount());
//		we already setted the order date and status
		
//		From the cart we are looping and getting cartItems and setting them to orderItems object
		Set<OrderItem> orderItems = cart.getItems().stream().map(cartItem -> {
			OrderItem orderItem = new OrderItem();
			orderItem.setOrder(order);
			orderItem.setMenuItem(cartItem.getMenuItem());
			orderItem.setQuantity(cartItem.getQuantity());
			orderItem.setPrice(cartItem.getMenuItem().getPrice() * orderItem.getQuantity());
			
			return orderItem;
		}).collect(Collectors.toSet());
		
//		setting orderItems(cartItems) to order (orderItems)
		order.setItems(orderItems);

//		Set order status SUCESS
		order.setOrderStatus("SUCCESS");
		
//		save the Order to the Order table
		Order savedOrder = orderRepository.save(order);
		
//		cart inactive after order success
		cart.setActive(false);
		
//		Saving empty cart
		cartRepository.save(cart);
		
		log.info("Order placed Succesfully by {}",email);
		
		return savedOrder;
	}

	public List<Order> myOrders(String email) {
		
		log.info("Order History fetching for User {}",email);
		return orderRepository.findByUserEmail(email);
	}
	
}
