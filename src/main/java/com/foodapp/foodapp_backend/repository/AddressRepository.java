package com.foodapp.foodapp_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.foodapp_backend.entity.Address;
import com.foodapp.foodapp_backend.entity.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
	
	List<Address>findByUser(User user);
	
	Optional<Address>findByUserAndIsDefault(User user, boolean isDefault);
	
	Optional<Address> findByIdAndUser(Long id, User user);
}
