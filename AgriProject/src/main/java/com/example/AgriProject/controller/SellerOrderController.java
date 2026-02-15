package com.example.AgriProject.controller;

import com.example.AgriProject.dto.SellerOrderDto;
import com.example.AgriProject.service.SellerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342")
public class SellerOrderController {

    private final SellerOrderService service;

    @GetMapping("seller/order/{sellerId}")
    public List<SellerOrderDto> getSellerOrders(@PathVariable Long sellerId) {
        return service.getPendingOrders(sellerId);
    }

    @PostMapping("seller/order/printed/{orderId}")
    public void markPrinted(@PathVariable Long orderId) {
        service.markOrderAsPrinted(orderId);
    }
}
