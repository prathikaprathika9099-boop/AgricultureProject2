package com.example.AgriProject.dto;

import com.example.AgriProject.Enumerations.OrderStatus;
import com.example.AgriProject.entity.OrderAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class OrderHistoryDto {
    private Long id;
    private LocalDateTime orderDate;
    private Double totalAmount;

    private String razorpayOrderId;
    private String razorpayPaymentId;

    private OrderStatus status;
    private OrderAddress address;
}
