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

import com.foodapp.foodapp_backend.entity.MenuCategory;
import com.foodapp.foodapp_backend.entity.MenuItem;
import com.foodapp.foodapp_backend.repository.MenuCategoryRepository;
import com.foodapp.foodapp_backend.repository.MenuItemRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MenuItemService {
	
	@Autowired
	MenuCategoryRepository menuCategoryRepository;
	
	@Autowired
	MenuItemRepository menuItemRepository;
	
	@Value("${file.upload-dir}")
	private String UPLOAD_DIR;
	
	@Value("${app.base-url}")
    private String BASE_URL;
	
	
	// Utility method to prepend the path for the frontend
    private MenuItem prependImagePath(MenuItem item) {
        if (item.getImageUrl() != null && !item.getImageUrl().isEmpty()) {
            
            String url = item.getImageUrl();
            
            // 1. External URL Check
            if (url.startsWith("http://") || url.startsWith("https://")) {
                // If it's an external URL, return as is.
                return item;
            }
            
            // 2. Local URL Construction (The FIX)
            // If it is a local file name (no prefix or just /images/), prepend the full BASE_URL
            if (!url.startsWith(BASE_URL)) {
                
                // If the URL already has /images/ (from previous calls), remove it before adding the full path
                if (url.startsWith("/images/")) {
                    url = url.substring("/images/".length());
                }
                
                // Construct the full absolute URL
                item.setImageUrl(BASE_URL + "/images/" + url);
            }
        }
        return item;
    }
	
	
	public MenuItem addNewItem(long categoryId, MenuItem menuItem, MultipartFile imageFile) {
		
		MenuCategory category = menuCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));
		
		// logic for saving image
		if (imageFile != null && !imageFile.isEmpty()) {
            try {
                // if folder is not exist create
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) {                                                                                          
                    uploadDir.mkdirs();
                } 

                // Unique filename create
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                log.info("File name with randomstring {}",fileName);
                
                
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                log.info("file path {}",filePath);

                // copying File in server
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // creating webPath for saving in db
                String webPath = fileName; 
                log.info("db path {}",webPath);
                menuItem.setImageUrl(webPath); // db path

            } catch (Exception e) {
                log.error("Image upload failed", e);
                throw new RuntimeException("Could not store image");
            }
        } 
		else {
			menuItem.setImageUrl(null); 
		}

		menuItem.setMenuCategory(category);
		
		log.info("{} Menu Item saved to Category {}", menuItem.getName(), category.getName());
		MenuItem savedItem = menuItemRepository.save(menuItem);
		return prependImagePath(savedItem);
	}
	

	public List<MenuItem> Allitems() {
		log.info("Fetching all Menu Items");
		// Map function added to prepend the image path
		return menuItemRepository.findByActiveTrue().stream()
                .map(this::prependImagePath)
                .collect(Collectors.toList());
	}


	public List<MenuItem> byCategoryId(long categoryId) {
		log.info("Fetching item by CategoryId ");
		// Map function added to prepend the image path
		return menuItemRepository.findByMenuCategoryIdAndActiveTrue(categoryId).stream()
                .map(this::prependImagePath)
                .collect(Collectors.toList());
	}


	public MenuItem updateItem(long itemId, String name, Double price, String description, MultipartFile imageFile, Double rating) {
	    
	    MenuItem oldItem = menuItemRepository.findById(itemId)
	            .orElseThrow(() -> new RuntimeException("Item not found with Id"));
	
	    // 1. Name Update
	    if(name != null && !name.isEmpty()) {
	        oldItem.setName(name);
	    }
	    
	    // 2. Price Update (Double wrapper class kabatti null check chestunnam)
	    if(price != null && price > 0) {
	        oldItem.setPrice(price);
	    }
	    
	    // 3. Description Update
	    if(description != null && !description.isEmpty()) {
	        oldItem.setDescription(description);
	    }
	    
	    if(rating != null && rating >0) {
	    	oldItem.setRating(rating);
	    }
	
	    // 4. Image Update Logic (Same old logic)
	    if (imageFile != null && !imageFile.isEmpty()) {
	        try {
	            File uploadDir = new File(UPLOAD_DIR);
	            if (!uploadDir.exists()) {            	
	            	uploadDir.mkdirs();
	            }
	
	            String originalName = imageFile.getOriginalFilename();
	            log.info("Original name {}",originalName);
	            
		        String cleanFileName = originalName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
		        log.info("Clean File name {}",cleanFileName);
	
		        String fileName = UUID.randomUUID().toString() + "_" + cleanFileName;
		        log.info("File name with random string {}", fileName);
		        
		        Path filePath = Paths.get(UPLOAD_DIR + fileName);
		        log.info("Path to store in the sever {}", filePath);
		        
		        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
		         
		        oldItem.setImageUrl(fileName); // DB update
		         
	        } catch (Exception e) {
	            log.error("Image update failed", e);
	            throw new RuntimeException("Could not update image");
	        }
	    }
	    
	    log.info("Updated Menu Item: {}", oldItem.getName());
	    MenuItem updatedItem = menuItemRepository.save(oldItem);
        // Return the item with the full path for response
	    return prependImagePath(updatedItem);
	}


	public String deleteItem(long itemId) {
		
		MenuItem menuItem = menuItemRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Item not found with id" + itemId + "to delte"));
		
		menuItem.setActive(false);
		menuItemRepository.save(menuItem);
		log.info("Item Succesfully Soft Deleted");
		return "Item succesfully Soft Deleted";
	}


	public List<MenuItem> searchMenuItem(String query) {
		
        log.info("Fetching item by search query: {}", query);
		return menuItemRepository.findByNameContainingIgnoreCase(query).stream()
                .map(this::prependImagePath)
                .collect(Collectors.toList());
	}


	public List<MenuItem> getArchieved() {
		log.info("Getting Soft deleted Items");
		return menuItemRepository.findByActiveFalse().stream()
                .map(this::prependImagePath)
                .collect(Collectors.toList());
	}


	public String activate(Long itemId) {
		
		MenuItem menuItem = menuItemRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Item not found with id"));
		
		menuItem.setActive(true);
		menuItemRepository.save(menuItem);
		log.info("Item Activated Succesfully id {}", itemId);
		
		return "Item Activated Succesfully";
		
	}


}
