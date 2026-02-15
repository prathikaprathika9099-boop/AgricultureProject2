package com.example.AgriProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ProductUploadDto {
    private String name;
    private MultipartFile image;
    private double cost;
    private String description;
    private Long userId;
    private int stock;
}
