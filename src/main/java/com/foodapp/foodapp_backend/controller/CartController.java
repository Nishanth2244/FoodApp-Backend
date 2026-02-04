package com.foodapp.foodapp_backend.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.dto.AddToCartRequest;
import com.foodapp.foodapp_backend.entity.Cart;
import com.foodapp.foodapp_backend.service.CartService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/cart")
public class CartController {
	
	@Autowired
	private CartService cartService;
	
	@PostMapping("/addItem")
	public Cart addToCart(@RequestBody AddToCartRequest addToCartRequest, Principal principal) {
		
		String email = principal.getName();
		
		Cart updatedCart = cartService.addItemsToCart(email, addToCartRequest.getMenuItemId(), addToCartRequest.getQuantity());
		log.info("Items are added to the Cart by {} user",email);
		return updatedCart;
	}
	
	@GetMapping("/myCart")
	public Cart getCart(Principal principal) {
		String email = principal.getName();
		log.info("Fetching Cart for the user {} ",email);
		return cartService.myCartitems(email);
		
	}
	
	@PutMapping("/decrease/{cartItemId}")
	public Cart decreaseCount(@PathVariable Long cartItemId, Principal principal) {
		
		String email = principal.getName();
		log.info("Decresing the Item Quanity of {} by {}",cartItemId,email);
		return cartService.decreaseItemQuantity(cartItemId,email);
	}
}
