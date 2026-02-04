package com.foodapp.foodapp_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.entity.Wishlist;

@Repository
public interface WishListRepository extends JpaRepository<Wishlist, Long>{

	Optional<Wishlist> findByUser(User user);


}
