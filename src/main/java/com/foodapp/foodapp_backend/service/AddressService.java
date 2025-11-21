package com.foodapp.foodapp_backend.service;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.foodapp.foodapp_backend.dto.AddressRequestDTO;
import com.foodapp.foodapp_backend.entity.Address;
import com.foodapp.foodapp_backend.entity.User;
import com.foodapp.foodapp_backend.repository.AddressRepository;
import com.foodapp.foodapp_backend.repository.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AddressService {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AddressRepository addressRepository;

	public Address addNewAddress(AddressRequestDTO addressRequestDTO, String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("user Noot Found with :"+email));
		
		Address address = new Address();
		address.setRecipentName(addressRequestDTO.getRecipentName());
		address.setStreet(addressRequestDTO.getStreet());
		address.setCity(addressRequestDTO.getCity());
		address.setState(addressRequestDTO.getState());
		address.setZipcode(addressRequestDTO.getZipcode());
		address.setCountry(addressRequestDTO.getCounrty());
		address.setUser(user);
		
		List<Address> existingAddresses = addressRepository.findByUser(user);
		
		if(existingAddresses.isEmpty()) {
			address.setDefault(true);
			log.info("First address added for user {} and set as default.", email);
		}
		
		log.info("New Address added succesfully by {}", email);
		return addressRepository.save(address);
	}

	public Address setDefaultAdd(Long addressId, String email) {
		
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new RuntimeException("User Not Found With Email: "+email));
		
		Optional<Address> currentDefaultAddress = addressRepository.findByUserAndIsDefault(user, true);
		
		if(currentDefaultAddress.isPresent()) {
			Address currentDefault = currentDefaultAddress.get();
			if(!currentDefault.equals(addressId)) {
				currentDefault.setDefault(false);
				addressRepository.save(currentDefault);
				log.info("Old default address {} unset for user {}.", currentDefault.getId(), email);
			}
			else {
				return currentDefault;
			}
			
		}
		
		Address newDefault = addressRepository.findByIdAndUser(addressId, user)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user."));

        newDefault.setDefault(true);
        log.info("Address {} set as new default for user {}.", addressId, email);
        
        return addressRepository.save(newDefault);
	}

	public String delete(String email, Long addressId) {
		
		Address address = addressRepository.findById(addressId)
				.orElseThrow(() -> new RuntimeException("User Not Found with Id: "+email));
		
		if(!address.getUser().getEmail().equals(email)) {
			throw new RuntimeException("Not authorized to delete Address");
		}
		
		log.info("Address deleted succesfully by "+email+ "Id is "+addressId);
		addressRepository.deleteById(addressId);
		return "Address deleted succesfully";
	}

}


