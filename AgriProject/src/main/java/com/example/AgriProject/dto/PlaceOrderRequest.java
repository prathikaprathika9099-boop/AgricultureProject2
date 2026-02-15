package com.example.AgriProject.dto;

import com.example.AgriProject.entity.OrderAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class PlaceOrderRequest {
    private OrderAddress address;
    private Double shippingFee;
}
