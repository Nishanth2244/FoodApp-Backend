package com.foodapp.foodapp_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.PutExchange;

import com.foodapp.foodapp_backend.entity.MenuItem;
import com.foodapp.foodapp_backend.service.MenuItemService;

@RestController
@RequestMapping("/items")
public class MenuItemController {
	
	@Autowired
	private MenuItemService menuItemService;
	
//	Adding an MenuItem to the Category ex:Chicken Biryani to Biryani category
	@PostMapping("/addItem/{categoryId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public MenuItem addItem(@PathVariable long categoryId, @RequestBody MenuItem menuItem) {
		return menuItemService.addNewItem(categoryId,menuItem);
	}
	
//	Fetching all items in Menu
	@GetMapping("/allItems")
	public List<MenuItem> getAllItems(){
		return menuItemService.Allitems();
	}
	
//	Fetching Items by CategoryId
	@GetMapping("/ByCategory/{categoryId}")
	public List<MenuItem> itemsByCategoryId(@PathVariable long categoryId){
		return menuItemService.byCategoryId(categoryId);
	}
	
//	updating menu item
	@PutExchange("/update/{itemId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public MenuItem updateMenuItem(@PathVariable long itemId, @RequestBody MenuItem updatedMenu) {
		return menuItemService.updateItem(itemId,updatedMenu);
	}
	
//	Deleting Menu Item 
	@DeleteMapping("/delete/{itemId}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public String deleteMenuItem(@PathVariable long itemId) {
		return menuItemService.deleteItem(itemId);
	}

}
