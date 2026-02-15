package com.example.AgriProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class CartDto {
    private Long cartId;
    private Long userId;
    private List<CartItemDto> items;
    private double totalAmount;
}
