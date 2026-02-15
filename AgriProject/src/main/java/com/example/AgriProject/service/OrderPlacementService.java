package com.example.AgriProject.service;

import com.example.AgriProject.Enumerations.OrderStatus;
import com.example.AgriProject.dto.PaymentConfirmRequest;
import com.example.AgriProject.entity.*;
import com.example.AgriProject.repository.CartRepository;
import com.example.AgriProject.repository.OrderRepository;
import com.example.AgriProject.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderPlacementService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void reserveStockForOrder(Long userId, Long addressId, double amount) {
        Cart cart=cartRepository.findByUserId(userId)
                .orElseThrow(()->new RuntimeException("Cart not found"));

        for(CartItem item:cart.getItems()){
            Product product=productRepository.findForUpdate(item.getProduct().getId());

            if(product.getStock()<item.getQuantity()){
                throw new RuntimeException("Insufficient stock for "+product.getName());
            }

            product.setReservedStock(product.getReservedStock()+item.getQuantity());
        }
        
        cart.setTotalAmount(amount);
        cartRepository.save(cart);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void confirmOrder(PaymentConfirmRequest request) {
        Cart cart=cartRepository.findByUserId(request.getUserId())
                .orElseThrow(() -> new RuntimeException("Cart not found"));


        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(cart.getTotalAmount());
        order.setRazorpayOrderId(request.getRazorpayOrderId());
        order.setRazorpayPaymentId(request.getRazorpayPaymentId());
        order.setStatus(OrderStatus.PAID);

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findForUpdate(item.getProduct().getId());

            product.setStock(product.getStock() - item.getQuantity());
            product.setReservedStock(product.getReservedStock() - item.getQuantity());

        }

        orderRepository.save(order);

        cart.getItems().clear();
        cart.setTotalAmount(0);
        cartRepository.save(cart);
    }

    @Transactional
    public void releaseStockIfPaymentFails(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        for (CartItem item : cart.getItems()) {
            Product product = productRepository.findForUpdate(item.getProduct().getId());

            // Release the lock on stock by resetting reservedStock
            product.setReservedStock(product.getReservedStock() - item.getQuantity());
        }

        cartRepository.save(cart);
    }
}
