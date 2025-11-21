package com.foodapp.foodapp_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.foodapp.foodapp_backend.entity.Offer;
import com.foodapp.foodapp_backend.service.OfferService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/offer")
public class OfferController {
	
	@Autowired
	private OfferService offerService;

	@PostMapping(value = "/addOffer", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Offer addOffer(@RequestParam("title") String title,
							@RequestParam("description") String description,
							@RequestParam("image") MultipartFile imageFile) {
		
		log.info("Request came to Add New Offer {}", title);
		return offerService.addNewOffer(title, description, imageFile);
	}
	
	@PutMapping(value = "/updateOffer/{offerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public Offer updateOffer(@PathVariable Long offerId,
							@RequestParam(value = "title",required = false) String title,
							@RequestParam(value = "description", required = false) String desciption,
							@RequestParam(value = "imageUrl", required = false) MultipartFile imageFile,
							@RequestParam(value = "active", required = false) Boolean isActive) {
		
		log.info("Rerquest camme to Update offer {}", offerId);
		return offerService.updateOffer(offerId,title, desciption, imageFile, isActive);
	}
	
	@DeleteMapping("/deleteOffer")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String deleteOffer(@PathVariable Long offerId) {
		
		log.info("Request came to delete Offer {}", offerId);
		return offerService.deleteOffer(offerId);
	}
	
	@GetMapping("/getActiveOffers")
	public List<Offer> getActiveOffers(){
		
		log.info("request came to fetch all the offers");
		return offerService.getAllActiveOffers();
	}
}
