package com.foodapp.foodapp_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.entity.MenuCategory;
import com.foodapp.foodapp_backend.repository.MenuCategoryRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MenuCategoryService {
	
	@Autowired
	MenuCategoryRepository menuCategoryRepository;

	public List<MenuCategory> getAll() {
		log.info("fetching all Categories");
		return menuCategoryRepository.findAll();
	}

	public MenuCategory addNewCategory(MenuCategory menuCategory) {
		
		if(menuCategoryRepository.existsByName(menuCategory.getName())) {
			throw new RuntimeException("Category already exists");
		}
		
		log.info("New Category is added");
		return menuCategoryRepository.save(menuCategory);
	}

	public MenuCategory updateCat(long id, MenuCategory updtCategory) {
		
		MenuCategory existing = menuCategoryRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Category Not found"));
		
		existing.setName(updtCategory.getName());
		log.info("Category updated with name {} ",updtCategory.getName());
		return menuCategoryRepository.save(existing);
	}

	public void deleteCat(long id) {
		menuCategoryRepository.deleteById(id);
		log.info("Category Deleted Succesfully");
		
	}
	
	
}
