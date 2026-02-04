package com.foodapp.foodapp_backend.entity;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@Entity
@Table(name = "wishlists", uniqueConstraints = {
    // one user has one wishlist so(UiniqueConstraint)
    @UniqueConstraint(columnNames = {"user_id"}) 
})
public class Wishlist {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	// Wishlist and user link 
	@ManyToOne
	@JoinColumn(name = "user_id") 
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	private User user;
	
	// Wishlist items list
	@OneToMany(mappedBy = "wishlist", cascade = CascadeType.ALL, orphanRemoval = true)
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@JsonManagedReference
	private Set<WishlistItem> items = new HashSet<>(); 

}