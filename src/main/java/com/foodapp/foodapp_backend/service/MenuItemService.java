package com.foodapp.foodapp_backend.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.foodapp.foodapp_backend.entity.MenuCategory;
import com.foodapp.foodapp_backend.entity.MenuItem;
import com.foodapp.foodapp_backend.repository.MenuCategoryRepository;
import com.foodapp.foodapp_backend.repository.MenuItemRepository;

import lombok.experimental.var;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MenuItemService {
	
	@Autowired
	MenuCategoryRepository menuCategoryRepository;
	
	@Autowired
	MenuItemRepository menuItemRepository;

	public MenuItem addNewItem(long categoryId, MenuItem menuItem) {
		
		MenuCategory category = menuCategoryRepository.findById(categoryId)
				.orElseThrow(() -> new RuntimeException("Category not found"));
		
		menuItem.setMenuCategory(category);
		
		log.info("{} Menu Item saved to Category {}",menuItem.getName(),category);
		return menuItemRepository.save(menuItem);
	}
	

	public List<MenuItem> Allitems() {
		log.info("Fetching all Menu Items");
		return menuItemRepository.findAll();
	}


	public List<MenuItem> byCategoryId(long categoryId) {
		log.info("Fetching item by CategoryId ");
		return menuItemRepository.findByMenuCategoryId(categoryId);
	}


	public MenuItem updateItem(long itemId, MenuItem updatedItem) {
		
		MenuItem oldItem = menuItemRepository.findById(itemId)
				.orElseThrow(() -> new RuntimeException("Item not found with Id"));
		
		oldItem.setName(updatedItem.getName());
		oldItem.setPrice(updatedItem.getPrice());
		oldItem.setDescription(updatedItem.getDescription());
		oldItem.setImageUrl(updatedItem.getImageUrl());
		
		log.info("Updating Menu Item ");
		
		return menuItemRepository.save(oldItem);
		
	}


	public String deleteItem(long itemId) {
		menuItemRepository.deleteById(itemId);
		log.info("Deleting Menu Item id: {}",itemId);
		return "Item deleted Succesfully";
	}

}
