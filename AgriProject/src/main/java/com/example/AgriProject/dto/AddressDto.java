package com.example.AgriProject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class AddressDto {
    private Long id;
    private String fullName;
    private String phone;
    private String house;
    private String street;
    private String city;
    private String state;
    private String pincode;
}
