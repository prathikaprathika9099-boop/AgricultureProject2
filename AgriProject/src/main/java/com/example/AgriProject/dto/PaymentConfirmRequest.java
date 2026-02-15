package com.example.AgriProject.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmRequest {
    private Long userId;
    private Long addressId;
    private Double amount;
    private String razorpayOrderId;
    private String razorpayPaymentId;
}
