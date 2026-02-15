package com.example.AgriProject.repository;

import com.example.AgriProject.Enumerations.OrderStatus;
import com.example.AgriProject.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem ,Long> {
    List<OrderItem> findBySellerIdAndOrder_StatusNotAndOrder_PrintedFalse(Long sellerId, OrderStatus status);
}
