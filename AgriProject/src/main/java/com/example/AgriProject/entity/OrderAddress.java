package com.example.AgriProject.entity;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderAddress {
    private String fullName;
    private String phone;
    private String house;
    private String street;
    private String city;
    private String state;
    private String pincode;
}
