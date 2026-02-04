package com.foodapp.foodapp_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.foodapp_backend.entity.MenuItem;
import com.foodapp.foodapp_backend.entity.Wishlist;
import com.foodapp.foodapp_backend.entity.WishlistItem;

@Repository
public interface WishListItemRepository extends JpaRepository<WishlistItem, Long>{
	
	Optional<WishlistItem> findByWishlistAndMenuItem(Wishlist wishlist, MenuItem menuItem);
	
	Optional<WishlistItem> findByIdAndWishlist(Long id, Wishlist wishlist);
}
