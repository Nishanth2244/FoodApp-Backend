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

import com.foodapp.foodapp_backend.entity.MenuItem;
import com.foodapp.foodapp_backend.service.MenuItemService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/items")
public class MenuItemController {
	
	@Autowired
	private MenuItemService menuItemService;
	
//	Adding an MenuItem to the Category ex:Chicken Biryani to Biryani category
	@PostMapping(value = "/addItem/{categoryId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public MenuItem addItem(@PathVariable long categoryId,
			
			@RequestParam("name") String name,
			@RequestParam("price") double price,
			@RequestParam("description") String description,
			@RequestParam("imageFile") MultipartFile imageFile,
			@RequestParam("rating") Double rating) {

			MenuItem menuItem = new MenuItem();
			menuItem.setName(name);
			menuItem.setPrice(price);
			menuItem.setDescription(description);
			menuItem.setRating(rating);
			
			log.info("Request came to add a new menu Item {}",name);
			
			return menuItemService.addNewItem(categoryId,menuItem,imageFile);
		}
	
//	Fetching all items in Menu
	@GetMapping("/allItems")
	public List<MenuItem> getAllItems(){
		log.info("Request to fetch all menu Items");
		return menuItemService.Allitems();
	}
	
//	Fetching Items by CategoryId
	@GetMapping("/ByCategory/{categoryId}")
	public List<MenuItem> itemsByCategoryId(@PathVariable long categoryId){
		return menuItemService.byCategoryId(categoryId);
	}
	
//	updating menu item
	@PutMapping(value = "/update/{itemId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public MenuItem updateMenuItem(@PathVariable long itemId,
	        @RequestParam(value = "name", required = false) String name,
	        @RequestParam(value = "price", required = false) Double price,
	        @RequestParam(value = "description", required = false) String description,
	        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
	        @RequestParam(value = "rating", required = false) Double rating) {
			
			log.info("request to update an Item");
			return menuItemService.updateItem(itemId, name, price, description, imageFile, rating);
	}
	

//	Searching an Item
	@GetMapping("/search")
	public List<MenuItem> serachItems(@RequestParam String query){
		return menuItemService.searchMenuItem(query);
	}
	
	
	
//	Soft delete Menu Item 
	@PutMapping("/delete/{itemId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String deleteMenuItem(@PathVariable long itemId) {
		return menuItemService.deleteItem(itemId);
	}
	
	
	
//	Activate menu item
	@PutMapping("/activate/{itemId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String activateItem(@PathVariable Long itemId) {
		return menuItemService.activate(itemId);
	}
	

//	Get Soft deleted items
	@GetMapping("/archieved")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public List<MenuItem> getSoftItems(){
		return menuItemService.getArchieved();
	}
	
}
