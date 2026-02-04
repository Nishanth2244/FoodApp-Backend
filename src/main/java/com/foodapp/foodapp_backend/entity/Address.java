package com.foodapp.foodapp_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
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
@Table(name = "addresses")
public class Address {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String recipentName;
	private String street;
	private String city;
	private String state;
	private String zipcode;
	private String country;
	
	@Column(columnDefinition = "boolean default false")
	private boolean isDefault = false;
	
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	@EqualsAndHashCode.Exclude
    @ToString.Exclude
    @JsonBackReference
	private User user;

}
