package com.foodapp.foodapp_backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.foodapp_backend.entity.Offer;
import com.foodapp.foodapp_backend.entity.Order;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {
	
	List<Offer> findByIsActive(Boolean isActive);

}
