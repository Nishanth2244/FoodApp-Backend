package com.foodapp.foodapp_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodapp.foodapp_backend.entity.MenuCategory;
import com.foodapp.foodapp_backend.entity.MenuItem;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {

//	List<MenuItem> findByMenuCategoryId(long categoryId);
	
//	List<MenuItem> findByName(String name);
	
	List<MenuItem> findByNameContainingIgnoreCase(String name);
	
	List<MenuItem> findByMenuCategoryIdAndActiveTrue(long categoryId);

	List<MenuItem> findByActiveTrue();

	List<MenuItem> findByActiveFalse();

}
