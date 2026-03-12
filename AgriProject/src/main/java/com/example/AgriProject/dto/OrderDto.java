package com.example.AgriProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDto {

    private Long id;
    private String fullName;
    private String phone;
    private String house;
    private String street;
    private String city;
    private String state;
    private String pincode;
    private Double totalAmount;
    private List<OrderItemDto> items;

}