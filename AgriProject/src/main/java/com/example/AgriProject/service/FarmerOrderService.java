package com.example.AgriProject.service;

import com.example.AgriProject.dto.OrderDto;
import com.example.AgriProject.dto.OrderItemDto;
import com.example.AgriProject.entity.Order;
import com.example.AgriProject.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
public class FarmerOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public List<OrderDto> getUnprintedOrders(Long farmerId) {

        List<Order> orders =
                orderRepository.findUnprintedOrdersForSeller(farmerId);

        return orders.stream().map(order -> {

            List<OrderItemDto> farmerItems =
                    order.getItems().stream()
                            .filter(item -> item.getSeller().getId().equals(farmerId))
                            .map(item -> new OrderItemDto(
                                    item.getProductName(),
                                    item.getQuantity(),
                                    item.getPrice()
                            ))
                            .toList();
            double totalAmount = order.getTotalAmount();

            return new OrderDto(
                    order.getId(),
                    order.getAddress().getFullName(),
                    order.getAddress().getPhone(),
                    order.getAddress().getHouse(),
                    order.getAddress().getStreet(),
                    order.getAddress().getCity(),
                    order.getAddress().getState(),
                    order.getAddress().getPincode(),
                    totalAmount,
                    farmerItems
            );
        }).toList();
    }

    @Transactional
    public void markOrderAsPacked(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setPrinted(true);
    }
}