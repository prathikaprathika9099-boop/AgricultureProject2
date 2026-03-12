package com.example.AgriProject.controller;

import com.example.AgriProject.config.AuthUtil;
import com.example.AgriProject.dto.OrderDto;
import com.example.AgriProject.service.FarmerOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342")
public class FarmerOrderController {

    @Autowired
    private FarmerOrderService farmerOrderService;

    @Autowired
    private AuthUtil authUtil;

    @GetMapping("/farmer/unprinted-orders")
    public ResponseEntity<List<OrderDto>> getUnprintedOrders(
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        Long farmerId = authUtil.getUserIdFromToken(token);

        return ResponseEntity.ok(
                farmerOrderService.getUnprintedOrders(farmerId)
        );
    }

    @PutMapping("/farmer/mark-packed/{orderId}")
    public ResponseEntity<?> markPacked(@PathVariable Long orderId) {

        farmerOrderService.markOrderAsPacked(orderId);
        return ResponseEntity.ok("Marked as packed");
    }
}