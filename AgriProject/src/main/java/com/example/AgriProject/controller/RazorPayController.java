package com.example.AgriProject.controller;


import com.example.AgriProject.Enumerations.OrderStatus;
import com.example.AgriProject.dto.PaymentConfirmRequest;
import com.example.AgriProject.entity.Address;
import com.example.AgriProject.entity.Order;
import com.example.AgriProject.entity.OrderAddress;
import com.example.AgriProject.entity.User;
import com.example.AgriProject.repository.AddressRepository;
import com.example.AgriProject.repository.OrderRepository;
import com.example.AgriProject.repository.UserRepository;
import com.example.AgriProject.service.CartService;
import com.example.AgriProject.service.OrderPlacementService;
import com.razorpay.RazorpayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.AgriProject.service.RazorPayService;


import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342")
public class RazorPayController {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    private final RazorPayService razorPayService;
    private final OrderPlacementService orderPlacementService;

    @PostMapping("/payment/create-order")
    public String createRazorpayOrder(@RequestParam int amount) {
        try {
            return razorPayService.createOrder(amount, "INR", "receipt_" + System.currentTimeMillis());
        } catch (RazorpayException e) {
            throw new RuntimeException(e);
        }
    }
    @PostMapping("/payment/confirm")
    public ResponseEntity<?> confirmPayment(
            @RequestBody PaymentConfirmRequest request) {

        try{
            orderPlacementService.confirmOrder(request);
            return ResponseEntity.ok("order confirmed");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error in confirming order");
        }
    }

    @PostMapping("/payment/reserve-stock")
    public ResponseEntity<String> reserveStock(@RequestParam Long userId,
                                               @RequestParam Long addressId,
                                               @RequestParam double amount){
        try {
            orderPlacementService.reserveStockForOrder(userId, addressId, amount);
            return ResponseEntity.ok("Stock reserved successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error reserving stock");
        }
    }

    @PostMapping("/payment/release-stock")
    public ResponseEntity<String> releaseStockIfPaymentFails(@RequestParam Long userId) {
        try {
            orderPlacementService.releaseStockIfPaymentFails(userId);
            return ResponseEntity.ok("Stock released successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error releasing stock");
        }
    }
}
