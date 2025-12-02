package com.foodapp.foodapp_backend.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value; // ✅ NEW: @Value kosam import
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.entity.MenuItem;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.entity.Wishlist;
import com.foodapp.foodapp_backend.entity.WishlistItem;
import com.foodapp.foodapp_backend.repository.MenuItemRepository;
import com.foodapp.foodapp_backend.repository.UserRepository;
import com.foodapp.foodapp_backend.repository.WishListItemRepository;
import com.foodapp.foodapp_backend.repository.WishListRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WishListService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private WishListRepository wishListRepository; 
	
	@Autowired
	private MenuItemRepository menuItemRepository;
	
	@Autowired
	private WishListItemRepository wishListItemRepository; 
	
	@Autowired
	private PushNotificationService pushNotificationService;
	

	@Value("${app.base-url}") 
    private String BASE_URL; 
    
    // Utility method to prepend the path for the frontend
    private MenuItem prependImagePath(MenuItem item) {
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            
            String url = item.getImageUrl();
            
            // 1. External URL Check
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return item;
            }
            
            // 2. Local URL Construction (The FIX)
            if (!url.startsWith(BASE_URL)) {
                
                if (url.startsWith("/images/")) {
                    url = url.substring("/images/".length());
                }
                
                item.setImageUrl(BASE_URL + "/images/" + url);
            }
        }
        return item;
    }
	
//	method to create wishlist
	private Wishlist getOrCreateWishlist(User user) {
        return wishListRepository.findByUser(user)
            .orElseGet(() -> {
                log.info("No wishlist found for user {}. Creating a new one.", user.getEmail());
                Wishlist newWishlist = new Wishlist();
                newWishlist.setUser(user);
                return wishListRepository.save(newWishlist);
            });
    }

	public Wishlist addItemToWishlist(Long menuItemId, String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not found with email"+ email));
		
		Wishlist wishlist = getOrCreateWishlist(user);
		
		MenuItem menuItem = menuItemRepository.findById(menuItemId)
				.orElseThrow(() -> new RuntimeException("Item not found in the menu :"+ menuItemId));
		
		Optional<WishlistItem> existingItem = wishListItemRepository.findByWishlistAndMenuItem(wishlist, menuItem);
		
		if(existingItem.isPresent()) {
			if(user.getExpoPushToken() != null && !user.getExpoPushToken().isEmpty()) {
				
				String title ="Wishlist";
				String body = existingItem.get().getMenuItem().getName() + " Item already in the wishlist" ;
				
				new Thread(() -> {
					pushNotificationService.sendNotification(user.getExpoPushToken(), title, body);
				}).start();
			}
			log.warn("{} Item is already in the WishList user {}", menuItemId, email);
			return wishlist;
		}else {
			WishlistItem wishlistItem = new WishlistItem();
			wishlistItem.setWishlist(wishlist);
			wishlistItem.setMenuItem(menuItem);
			
			wishlist.getItems().add(wishlistItem);
			wishListItemRepository.save(wishlistItem);
			log.info(" {} Item added to wishlist for user {}",menuItemId,email);
			
			if(user.getExpoPushToken() != null && !user.getExpoPushToken().isEmpty()) {
				
				String title ="Wishlist";
				
				String body = wishlistItem.getMenuItem().getName() +" added to wishlist" ;
				
				new Thread(() -> {
					pushNotificationService.sendNotification(user.getExpoPushToken(), title, body);
				}).start();
			}
		
			return wishlist;
		}		
	}

	
	public Wishlist getWishlist(String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not found "+ email));
		
		Wishlist wishlist = getOrCreateWishlist(user);
        
        // ✅ LOGIC: Prathi WishlistItem lo unna MenuItem ki prependImagePath apply cheyyali
        wishlist.getItems().forEach(wishlistItem -> {
            prependImagePath(wishlistItem.getMenuItem());
        });
		
        log.info("fetching wishlist for {}", email);
		return wishlist;
		
	}
	
public String removeItem(Long wishlistItemId, String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not found with : "+email ));
		
		Wishlist wishlist = getOrCreateWishlist(user);
		
		WishlistItem itemToRemove = wishListItemRepository.findByIdAndWishlist(wishlistItemId, wishlist)
				.orElseThrow(() -> new RuntimeException("Wishlist Item not found or does not belong to user."));
		
		wishlist.getItems().remove(itemToRemove);
		wishListItemRepository.delete(itemToRemove);
		
		log.info("Wishlist Item with id {} removed successfully for user {}", wishlistItemId, email);
		
		return "Item removed from wishlist successfully";
	}

}