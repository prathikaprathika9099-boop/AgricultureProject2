package com.example.AgriProject.controller;

import com.example.AgriProject.dto.OrderHistoryDto;
import com.example.AgriProject.dto.PlaceOrderRequest;
import com.example.AgriProject.entity.Order;
import com.example.AgriProject.entity.OrderAddress;
import com.example.AgriProject.repository.OrderRepository;
import com.example.AgriProject.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342")
public class OrderController {
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @GetMapping("/orders/user/{userId}")//for history page
    public List<Order> getUserOrders(@PathVariable Long userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    @GetMapping("/orders/unprinted")
    public List<Order> getUnprintedOrders(){
        return orderService.getUnprinted();
    }

    @PutMapping("/orders/{orderId}/print")
    public Order markOrderAsPrinted(@PathVariable Long orderId){
        return orderService.markOrderAsPrinted(orderId);
    }

    @PostMapping("/orders/place/{userId}") //cash on delivery
    public String placeOrder(@PathVariable Long userId, @RequestBody PlaceOrderRequest request){
        orderService.placeOrder(userId,request.getAddress(),request.getShippingFee());
        return "Order Placed Successfully";
    }
}
