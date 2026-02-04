package com.foodapp.foodapp_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.foodapp_backend.entity.Cart;
import com.foodapp.foodapp_backend.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
	
	List<Cart> findByUserAndActive(User user, boolean active);

}
