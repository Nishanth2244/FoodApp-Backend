package com.foodapp.foodapp_backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.entity.Cart;
import com.foodapp.foodapp_backend.entity.CartItem;
import com.foodapp.foodapp_backend.entity.MenuItem;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.CartItemRepository;
import com.foodapp.foodapp_backend.repository.CartRepository;
import com.foodapp.foodapp_backend.repository.MenuItemRepository;
import com.foodapp.foodapp_backend.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class CartService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CartRepository cartRepository;
	
	@Autowired
	private MenuItemRepository menuItemRepository;
	
	@Autowired
	private CartItemRepository cartItemRepository;
	
	@Transactional 
	public Cart addItemsToCart(String email, Long menuItemId, int quantity) {
		
		//finding the user from UserRepo
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User not Found with Email: "+email));
		
		Cart cart = cartRepository.findByUserAndActive(user, true)
				.orElseGet(() -> {
					Cart newCart = new Cart();
					newCart.setUser(user);
					newCart.setTotalAmount(0.0); 
					return cartRepository.save(newCart);
				});
		
		MenuItem menuItem = menuItemRepository.findById(menuItemId)
				.orElseThrow(() -> new RuntimeException("Menu item is not found"));
		
		Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getMenuItem().getId().equals(menuItemId))
                .findFirst();
		
		if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item); 
		} else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setMenuItem(menuItem);
            newItem.setQuantity(quantity);
            cart.getItems().add(newItem);
            cartItemRepository.save(newItem); 
        }

        double total = cart.getItems().stream()
                .mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
        
        cart.setTotalAmount(total);

        return cartRepository.save(cart);
	}

	
	
	public Cart myCartitems(String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("Employee Not Found with: "+email));
		
		Cart cart = cartRepository.findByUserAndActive(user, true)
				.orElseGet(() ->{
					Cart newCart = new Cart();
					newCart.setUser(user);
					newCart.setTotalAmount(0.0);
					return cartRepository.save(newCart);
				});
		
		return cart;
	}
	
	

	public Cart decreaseItemQuantity(Long cartItemId, String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not Found with Email"+email));
		
		Cart cart = cartRepository.findByUserAndActive(user, true)
				.orElseThrow(() -> new RuntimeException("Cart Not Found to User "+user));
		
		CartItem cartItem = cartItemRepository.findById(cartItemId)
				.orElseThrow(() -> new RuntimeException("Cart Item is not Found with id "+ cartItemId));
		
		
		if (!cart.getItems().contains(cartItem)) {
		    throw new RuntimeException("Unauthorized access to cartItem");
		}
		
		long currentQuantity = cartItem.getQuantity();
		
		if(currentQuantity >1 ) {
			cartItem.setQuantity(currentQuantity - 1);
			cartItemRepository.save(cartItem);
		}
		else {
			cart.getItems().remove(cartItem);
			cartItemRepository.delete(cartItem);
		}
		
		double total = cart.getItems().stream()
				.mapToDouble(item -> item.getMenuItem().getPrice() * item.getQuantity())
				.sum();
		cart.setTotalAmount(total);
		
		return cartRepository.save(cart);
	}
}