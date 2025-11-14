package com.foodapp.foodapp_backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.foodapp.foodapp_backend.entity.MenuCategory;
import com.foodapp.foodapp_backend.service.MenuCategoryService;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/categories")
public class MenuCategoryController {
	
	@Autowired
	private MenuCategoryService menuCategoryService;
	
	@GetMapping("/all")
	public List<MenuCategory> getAllCategories(){
		return menuCategoryService.getAll();
	}
	
	@PostMapping("/addCategory")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<MenuCategory> addCategory(@RequestBody MenuCategory menuCategory){
		MenuCategory menu = menuCategoryService.addNewCategory(menuCategory);
		return ResponseEntity.status(HttpStatus.CREATED).body(menu);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping("/update/{id}")
	public MenuCategory updateCategory(@PathVariable long id, @RequestBody MenuCategory menuCategory) {
		return menuCategoryService.updateCat(id,menuCategory);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping("/delete/{id}")
	public String deleteCategory(@PathVariable long id) {
		 menuCategoryService.deleteCat(id);
		 return "Category Deleted Succesfully";
	}
}
