package com.foodapp.foodapp_backend.dto;

import lombok.Data;

@Data
public class AddToCartRequest {
							
    private Long menuItemId;     // item to be added
    private int quantity;        // how many pieces
}
