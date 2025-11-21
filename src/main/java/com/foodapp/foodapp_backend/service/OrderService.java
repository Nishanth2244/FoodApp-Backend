package com.foodapp.foodapp_backend.service;

import java.lang.System.Logger;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.dto.OrderUpdateStatusDTO;
import com.foodapp.foodapp_backend.entity.Cart;
import com.foodapp.foodapp_backend.entity.CartItem;
import com.foodapp.foodapp_backend.entity.MenuItem;
import com.foodapp.foodapp_backend.entity.Order;
import com.foodapp.foodapp_backend.entity.OrderItem;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.CartRepository;
import com.foodapp.foodapp_backend.repository.OrderRepository;
import com.foodapp.foodapp_backend.repository.UserRepository;

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
	
	@Autowired
	private UserRepository userRepository;
	
	@Value("${app.base-url}")
    private String BASE_URL;
	
	// Utility method to prepend the path (Copied logic from MenuItemService)
    private MenuItem prependImagePath(MenuItem item) {
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            
            String url = item.getImageUrl();
            
            // 1. External URL Check
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return item;
            }
            
            // 2. Local URL Construction (The FIX)
            if (!url.startsWith(BASE_URL)) {
                
                // If the URL already has /images/ (from previous calls), remove it before adding the full path
                if (url.startsWith("/images/")) {
                    url = url.substring("/images/".length());
                }
                
                // Construct the full absolute URL
                item.setImageUrl(BASE_URL + "/images/" + url);
            }
        }
        return item;
    }
	
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
			
	        List<Order> orders = orderRepository.findByUserEmail(email);
	        
	        // ✅ Logic to update image URLs in all OrderItems of all Orders
	        orders.forEach(order -> {
	            order.getItems().forEach(orderItem -> {
	                // Apply the image path prepending logic to the MenuItem inside each OrderItem
	                prependImagePath(orderItem.getMenuItem());
	            });
	        });
	
			return orders;
		}

	
	public Order updateStatus(Long orderId, OrderUpdateStatusDTO orderUpdateStatusDTO) {
		
//		Finding the Order with orderId
		Order oldOrder = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with Id: "+orderId));
		
		log.info("Updating Order staus from {} to {}",oldOrder.getOrderStatus(),orderUpdateStatusDTO.getOrderStatus());
//		Setting the new Status
		oldOrder.setOrderStatus(orderUpdateStatusDTO.getOrderStatus());
		
		return orderRepository.save(oldOrder);
		
	}

	public Order cancelOrder(Long orderId, String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not found"));
		
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new RuntimeException("Order not found with Id:"+orderId));
		
		if(!order.getUser().getId().equals(user.getId())) {
			throw new RuntimeException("You cannot cancel someone else's order!");
		}
		
		if(!order.getOrderStatus().equals("PENDING") && !order.getOrderDate().equals("PREPARING") && !order.getOrderDate().equals("OUT FOR DELIVERY")) {
			log.warn("User {} This order cannot be cancelled as it is already: {}", email, order.getOrderStatus());
			throw new RuntimeException("This order cannot be cancelled as it is already: " + order.getOrderStatus());
		}
		
		
		order.setOrderStatus("CANCELLED"); 
		
		return orderRepository.save(order);
		
		
	}
	
}
