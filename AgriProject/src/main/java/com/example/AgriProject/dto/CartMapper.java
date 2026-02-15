package com.example.AgriProject.dto;

import com.example.AgriProject.entity.Cart;
import com.example.AgriProject.entity.CartItem;
import com.example.AgriProject.entity.Product;

import java.util.stream.Collectors;

public class CartMapper {

    public static CartDto toCartDto(Cart cart){
        CartDto cartDto=new CartDto();
        cartDto.setCartId(cart.getId());
        cartDto.setUserId(cart.getUser().getId());
        cartDto.setTotalAmount(cart.getTotalAmount());

        cartDto.setItems(
                cart.getItems()
                        .stream()
                        .map(CartMapper::toCartItemDTO)
                        .collect(Collectors.toList())
        );
        return cartDto;
    }
    private static CartItemDto toCartItemDTO(CartItem item) {
        CartItemDto dto = new CartItemDto();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setPrice(item.getPrice());
        dto.setSubtotal(item.getSubtotal());
        dto.setProduct(toProductDTO(item.getProduct()));
        return dto;
    }

    private static ProductDto toProductDTO(Product product) {
        ProductDto dto = new ProductDto();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setCost(product.getCost());
        dto.setImageUrl(product.getImageUrl());
        return dto;
    }
}
