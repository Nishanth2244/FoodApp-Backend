package com.foodapp.foodapp_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.foodapp_backend.entity.Order;
import com.foodapp.foodapp_backend.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
	
	List<Order> findByUserEmail(String email);
	
	List<Order> findByRider(User rider);
}
