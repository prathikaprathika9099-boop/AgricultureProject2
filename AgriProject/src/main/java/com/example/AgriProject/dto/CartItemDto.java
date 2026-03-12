package com.example.AgriProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CartItemDto {
    private Long id;
    private int quantity;
    private double price;
    private double subtotal;
    private ProductDto product;

    private int stock;
}
