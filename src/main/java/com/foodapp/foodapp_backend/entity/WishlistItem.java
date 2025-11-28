package com.foodapp.foodapp_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "wishlist_items")
public class WishlistItem {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	// Wishlist ki link
	@ManyToOne
	@JoinColumn(name = "wishlist_id")
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonBackReference
	private Wishlist wishlist; 
	
	// Item details
	@ManyToOne
	@JoinColumn(name = "menu_item_id")
	private MenuItem menuItem;		
}