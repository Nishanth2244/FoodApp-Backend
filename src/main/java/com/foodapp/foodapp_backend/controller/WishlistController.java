package com.foodapp.foodapp_backend.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.entity.Wishlist;
import com.foodapp.foodapp_backend.service.WishListService;

@RestController
@RequestMapping("/wishlist")
public class WishlistController {
	
	@Autowired
	private WishListService wishListService;

	@PostMapping("/addItem/{menuItemId}")
	public Wishlist addItemWishlist(@PathVariable Long menuItemId, Principal principal) {
		
		String email = principal.getName();
		return wishListService.addItemToWishlist(menuItemId, email);
	}
	
	
	@GetMapping("/getWishlist")
	public Wishlist getWishlist(Principal principal) {
		
		String email = principal.getName();
		return wishListService.getWishlist(email);
	}
	
	@DeleteMapping("/remove/{wishlistItemId}")
	public String removeItemFromWishlist(@PathVariable Long wishlistItemId, Principal principal) {
		
		String email = principal.getName();
		return wishListService.removeItem(wishlistItemId, email);
	}
}
