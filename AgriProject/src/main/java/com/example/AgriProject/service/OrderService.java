package com.example.AgriProject.service;

import com.example.AgriProject.Enumerations.OrderStatus;
import com.example.AgriProject.dto.OrderHistoryDto;
import com.example.AgriProject.entity.*;
import com.example.AgriProject.events.OrderPlacedEvent;
import com.example.AgriProject.repository.CartRepository;
import com.example.AgriProject.repository.OrderRepository;
import com.example.AgriProject.repository.ProductRepository;
import com.example.AgriProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public List<OrderHistoryDto> getOrdersByUser(Long userId) {//for cash on delivery

        return orderRepository.findByUserIdOrderByOrderDateDesc(userId)
                .stream()
                .map(order -> {
                    OrderHistoryDto dto = new OrderHistoryDto();
                    dto.setId(order.getId());
                    dto.setOrderDate(order.getOrderDate());
                    dto.setTotalAmount(order.getTotalAmount());
                    dto.setRazorpayOrderId(order.getRazorpayOrderId());
                    dto.setRazorpayPaymentId(order.getRazorpayPaymentId());
                    dto.setStatus(order.getStatus());
                    dto.setAddress(order.getAddress());
                    return dto;
                })
                .toList();
    }

    public List<Order> getUnprinted(){
        return orderRepository.findByStatusAndPrintedFalse(OrderStatus.PAID);
    }

    public Order markOrderAsPrinted(Long orderId){
        Order order=orderRepository.findById(orderId)
                .orElseThrow(()->new RuntimeException("Order not found"));

        order.setPrinted(true);
        return orderRepository.save(order);
    }


    @Retryable(
            value = {org.springframework.dao.OptimisticLockingFailureException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100)
    )
    @Transactional
    public void placeOrder(Long userId, OrderAddress address,Double shippingFee){//cash on delivery
        Cart cart=cartRepository.findByUserId(userId)
                .orElseThrow(()->new RuntimeException("Cart not found"));

        if(cart.getItems().isEmpty()){
            throw new RuntimeException("Cart is Empty");
        }

        User user=userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));

        Order order=new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PLACED);
        order.setAddress(address);
        order.setPrinted(false);

        double itemsTotal=cart.getTotalAmount();
        double shipFee=(shippingFee==null?0.0:shippingFee);

        List<Long> productIds = cart.getItems().stream()
                .map(ci -> ci.getProduct().getId())
                .toList();

        List<Product> lockedProducts = productRepository.findAllByIdInForUpdate(productIds);

        Map<Long, Product> productMap = lockedProducts.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));


        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {

            Product product = productMap.get(cartItem.getProduct().getId());
            if (product == null) throw new RuntimeException("Product not found");

            int qty = cartItem.getQuantity();

            if (product.getStock() < qty) {
                throw new RuntimeException("Not enough stock for: " + product.getName());
            }

            // reduce stock
            product.setStock(product.getStock() - qty);

            OrderItem item = new OrderItem();
            item.setProductName(product.getName());
            item.setPrice(product.getCost());
            item.setQuantity(qty);
            item.setSeller(product.getUser());
            item.setOrder(order);

            orderItems.add(item);
        }
        order.setItemsTotal(itemsTotal);
        order.setShippingFee(shipFee);
        order.setTotalAmount(itemsTotal + shipFee);

        order.setItems(orderItems);
        orderRepository.save(order); // cascades orderItems if mapping is correct
        productRepository.saveAll(lockedProducts);
        cart.getItems().clear();
        cart.setTotalAmount(0.0);
        cartRepository.save(cart);


        applicationEventPublisher.publishEvent(new OrderPlacedEvent(order.getId()));
    }
}
