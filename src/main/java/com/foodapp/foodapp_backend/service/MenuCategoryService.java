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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MenuCategoryService {
	
	@Autowired
	MenuCategoryRepository menuCategoryRepository;
	
	@Value("${file.upload-dir}")
	private String UPLOAD_DIR;
	
	@Value("${app.base-url}")
	private String BASE_URL;
	
	private MenuCategory prependImagePath(MenuCategory category) {
        if (category.getImageUrl() != null && !category.getImageUrl().isEmpty()) {
            String url = category.getImageUrl();
            
            // External URL (http/https) unte emi cheyoddu
            if (url.startsWith("http://") || url.startsWith("https://")) {
                return category;
            }
            
            // Local URL construction
            if (!url.startsWith(BASE_URL)) {
                // Duplicate '/images/' unte teesesthunnam
                if (url.startsWith("/images/")) {
                    url = url.substring("/images/".length());
                }
                // Full URL set chesthunnam
                category.setImageUrl(BASE_URL + "/images/" + url);
            }
        }
        return category;
    }
	
	

	// 4. Get All Categories 
	public List<MenuCategory> getAll() {
			log.info("fetching all Categories");
			return menuCategoryRepository.findAll().stream()
	                .map(this::prependImagePath) 
	                .collect(Collectors.toList());
		}
	

	public MenuCategory addNewCategory(String name, MultipartFile imageFile) {
		
		if(menuCategoryRepository.existsByName(name)) {
			throw new RuntimeException("Category already exists");
		}
		
		MenuCategory menuCategory = new MenuCategory();
		menuCategory.setName(name);
		
		if(imageFile != null && !imageFile.isEmpty()) {
			try {
				File uploadDir = new File(UPLOAD_DIR);
				if(!uploadDir.exists()) {
					uploadDir.mkdirs();
				}
				
				String fileName = UUID.randomUUID().toString() +"_"+ imageFile.getOriginalFilename();
				Path filePath = Paths.get(UPLOAD_DIR + fileName);
				
				Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
				
				menuCategory.setImageUrl(fileName);
			}
			catch (Exception e){
				log.info("cannot store the image");
				throw new RuntimeException("Could store the image");
			}
		}
		
		log.info("New Category is added {}", name);
		MenuCategory savedCategory = menuCategoryRepository.save(menuCategory);
		
		return prependImagePath(savedCategory);
	}

	public MenuCategory updateCat(long id, String name, MultipartFile imageFile) {
		
		MenuCategory existing = menuCategoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category Not found"));
		
		if(name != null && !name.isEmpty()) {
			existing.setName(name);
		}
		
		if(imageFile != null && !imageFile.isEmpty()) {
			try {
//				deleting old file name
				String oldFileName = existing.getImageUrl();
				
				if (oldFileName != null) {
                	try {
                    	// URL lo BASE_URL unte danni remove chesi clean filename theesukovali
                    	String cleanFileName = oldFileName;
                    	if(oldFileName.contains("/images/")) {
                    		cleanFileName = oldFileName.substring(oldFileName.lastIndexOf("/images/") + 8);
                    	}
                        Path oldPath = Paths.get(UPLOAD_DIR + cleanFileName);
                        Files.deleteIfExists(oldPath);
                    } catch (Exception e) {
                        log.warn("Failed to delete old category image");
                    }
                }
				
				// B. Kotha image save cheyandi
                File uploadDir = new File(UPLOAD_DIR);
                if (!uploadDir.exists()) uploadDir.mkdirs();

                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);

                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // C. DB lo update cheyandi
                existing.setImageUrl(fileName);
			}
			catch (Exception e) {
                log.error("Category Image update failed", e);
                throw new RuntimeException("Could not update image");
            }
		}
		
		log.info("Category updated with id: {} ", id);
        MenuCategory saved = menuCategoryRepository.save(existing);
		return prependImagePath(saved);
	}

	public void deleteCat(long id) {
		// Optional: Delete chese mundhu image kuda delete cheyochu
		MenuCategory category = menuCategoryRepository.findById(id).orElse(null);
		if(category != null && category.getImageUrl() != null) {
			try {
				String cleanFileName = category.getImageUrl();
            	if(cleanFileName.contains("/images/")) {
            		cleanFileName = cleanFileName.substring(cleanFileName.lastIndexOf("/images/") + 8);
            	}
				Path path = Paths.get(UPLOAD_DIR + cleanFileName);
				Files.deleteIfExists(path);
			} catch (Exception e) {
				log.warn("Could not delete image file during category deletion");
			}
		}
		
		menuCategoryRepository.deleteById(id);
		log.info("Category Deleted Succesfully");
	}
	
	
}
