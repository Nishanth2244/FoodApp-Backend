package com.foodapp.foodapp_backend.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.foodapp.foodapp_backend.entity.Offer;
import com.foodapp.foodapp_backend.repository.OfferRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class OfferService {
	
	@Value("${file.upload-dir}")
	private String UPLOAD_DIR;
	
	@Autowired
	private OfferRepository offerRepository;
	
	@Value("${app.base-url}")
    private String BASE_URL;
	
	// ✅ NEW: Utility method to prepend the full path
    private Offer prependOfferImagePath(Offer offer) {
        if (offer.getImageUrl() != null && !offer.getImageUrl().isEmpty()) {
            
            String url = offer.getImageUrl();
            
            // 1. External URL Check
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return offer;
            }
            
            // 2. Local URL Construction (The FIX)
            if (!url.startsWith(BASE_URL)) {
                
                // If the URL already has /images/ (from previous calls), remove it before adding the full path
                if (url.startsWith("/images/")) {
                    url = url.substring("/images/".length());
                }
                
                // Construct the full absolute URL
                offer.setImageUrl(BASE_URL + "/images/" + url);
            }
        }
        return offer;
    }
	
	
//	Utility Method For Saving Image in Server
	private String saveImage(MultipartFile imageFile) {
	    if (imageFile == null || imageFile.isEmpty()) {
	        return null; 
	    }
	    try {
	        File uploadDir = new File(UPLOAD_DIR);
	        if (!uploadDir.exists()) {
	            uploadDir.mkdirs();
	        }
	        String originalName = imageFile.getOriginalFilename();
	        String cleanFileName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
	        String fileName = UUID.randomUUID().toString() + "_" + cleanFileName;
	        
	        Path filePath = Paths.get(UPLOAD_DIR + fileName);
	        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
	        
	        return fileName; // Returns the webPath/dbPath
	    } catch (Exception e) {
	        log.error("Offer image upload failed", e);
	        throw new RuntimeException("Could not store offer image");
	    }
	}

	
//	Adding an Offer
	public Offer addNewOffer(String title, String description, MultipartFile imageFile) {
		
		Offer newOffer = new Offer();
		newOffer.setTitle(title);
		newOffer.setDescription(description);
		
		String imageUrl = saveImage(imageFile);
		newOffer.setImageUrl(imageUrl);
		
		log.info("New Offer Saved Succesfully {}", newOffer.getTitle());
		Offer savedOffer = offerRepository.save(newOffer);
        
        // Return with full URL
        return prependOfferImagePath(savedOffer);
	}

	
//	Update Offer
	public Offer updateOffer(Long offerId, String title, String desciption, MultipartFile imageFile, Boolean isActive) {
		
		Offer existingOffer = offerRepository.findById(offerId)
				.orElseThrow(() -> new RuntimeException("Offer not exist with id: "+offerId));
		
		if(title != null && !title.isEmpty()) {
			existingOffer.setTitle(title);
		}
		
		if(desciption != null  && !desciption.isEmpty()) {
			existingOffer.setDescription(desciption);
		}
		
		if(isActive != null) {
			existingOffer.setActive(isActive);
		}
		
		if(imageFile != null && !imageFile.isEmpty()) {
			log.info("Image changing if image is null");
			String imageUrl = saveImage(imageFile);
			existingOffer.setImageUrl(imageUrl);
		}
		
		log.info("Offer updated Succesfully {}",offerId);
		Offer updatedOffer = offerRepository.save(existingOffer);
        
        // Return with full URL
        return prependOfferImagePath(updatedOffer);
	}



//	Deleting Offer
	public String deleteOffer(Long offerId) {
		
		log.info("Offer deleted Succesfully {}",offerId);
		offerRepository.deleteById(offerId);
		return "offer Deleted Succesfully";
	}
	

//	Fetching all Activeoffers
	public List<Offer> getAllActiveOffers() {
		log.info("Fetching all the offers");
        // ✅ Apply URL prepending logic to all offers being returned
		return offerRepository.findByIsActive(true).stream()
                .map(this::prependOfferImagePath)
                .collect(Collectors.toList());
	}

}
