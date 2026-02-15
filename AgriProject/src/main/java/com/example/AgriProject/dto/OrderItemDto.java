package com.example.AgriProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderItemDto {
    private String productName;
    private int quantity;
    private double price;
}
