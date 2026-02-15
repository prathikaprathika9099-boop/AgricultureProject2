package com.example.AgriProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class SellerOrderDto {
    private Long orderId;
    private String customerName;
    private String phone;
    private String address;

    private String productName;
    private Integer quantity;
    private Double price;

    private Double totalAmount;
}
