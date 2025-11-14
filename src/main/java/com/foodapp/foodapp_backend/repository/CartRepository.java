package com.foodapp.foodapp_backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.foodapp_backend.entity.Cart;
import com.foodapp.foodapp_backend.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	
    Optional<Cart> findByUser(User user);


}
