package com.example.AgriProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductDto {
    private Long id;
    private String name;
    private double cost;
    private String imageUrl;
}
