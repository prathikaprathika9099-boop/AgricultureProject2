package com.example.AgriProject.service;

import com.example.AgriProject.Enumerations.OrderStatus;
import com.example.AgriProject.dto.SellerOrderDto;
import com.example.AgriProject.entity.Order;
import com.example.AgriProject.entity.OrderItem;
import com.example.AgriProject.repository.OrderItemRepository;
import com.example.AgriProject.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SellerOrderService {
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public List<SellerOrderDto> getPendingOrders(Long sellerId) {

        return orderItemRepository
                .findBySellerIdAndOrder_StatusNotAndOrder_PrintedFalse(
                        sellerId, OrderStatus.DELIVERED)
                .stream()
                .map(item -> {
                    SellerOrderDto dto = new SellerOrderDto();

                    Order o = item.getOrder();

                    dto.setOrderId(o.getId());
                    dto.setCustomerName(o.getAddress().getFullName());
                    dto.setPhone(o.getAddress().getPhone());
                    dto.setAddress(
                            o.getAddress().getHouse() + ", " +
                                    o.getAddress().getStreet() + ", " +
                                    o.getAddress().getCity()
                    );

                    dto.setProductName(item.getProductName());
                    dto.setQuantity(item.getQuantity());
                    dto.setPrice(item.getPrice());
                    dto.setTotalAmount(o.getTotalAmount());

                    return dto;
                })
                .toList();
    }

    public void markOrderAsPrinted(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow();
        order.setPrinted(true);
        orderRepository.save(order);
    }
}
