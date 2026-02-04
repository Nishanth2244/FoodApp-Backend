package com.foodapp.foodapp_backend.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.dto.AddressRequestDTO;
import com.foodapp.foodapp_backend.dto.UpdateAddressRequestDTO;
import com.foodapp.foodapp_backend.entity.Address;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.AddressRepository;
import com.foodapp.foodapp_backend.repository.UserRepository;
import com.foodapp.foodapp_backend.service.AddressService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/address")
public class AddressController {
	
	@Autowired
	private AddressService addressService;
	
	@Autowired
	private AddressRepository addressRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@PostMapping("/add")
	public Address addAddress(@RequestBody AddressRequestDTO addressRequestDTO, Principal principal) {
		
		String email = principal.getName();
		return addressService.addNewAddress(addressRequestDTO, email);
	}
	
	@GetMapping("/getAllAddresess")
	public List<Address> getAllAddresses(Principal principal){
		
		String email = principal.getName();
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not found with name: "+email));
		
		log.info("Fetching all the Address of the user {}", email);
		return addressRepository.findByUser(user);
	}
	
	@PutMapping("/setDefault/{addressId}")
	public Address setDefault(@PathVariable Long addressId, Principal principal) {
		
		String email = principal.getName();
		return addressService.setDefaultAdd(addressId, email);
	}
	
	
	@DeleteMapping("/delete/{addressId}")
	public String deleteAddress(@PathVariable Long addressId, Principal principal) {
		
		String email = principal.getName();
		return addressService.delete(email,addressId);
	}

	
	
	@PutMapping("/updateAddress/{addressId}")
	public Address updateAddress(@PathVariable Long addressId, @RequestBody UpdateAddressRequestDTO updateAddressRequestDTO, Principal principal) {
		
		String email = principal.getName();
		return addressService.updateAddress(addressId, updateAddressRequestDTO, email);
	}


}
