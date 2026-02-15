package com.example.AgriProject.controller;

import com.example.AgriProject.dto.CartDto;
import com.example.AgriProject.dto.UpdateCartDto;
import com.example.AgriProject.entity.Cart;
import com.example.AgriProject.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:63342")
public class CartController {

    private final CartService cartService;

    @PostMapping("/cart/add")
    public ResponseEntity<CartDto> addToCart(
            @RequestParam Long userId,
            @RequestParam Long productId,
            @RequestParam int quantity
    ){
        CartDto cartDto=cartService.addProductToCart(userId,productId,quantity);
        return ResponseEntity.ok(cartDto);
    }

    @GetMapping("/cart/user/{userId}")
    public ResponseEntity<CartDto> getUserCart(@PathVariable Long userId){
       return ResponseEntity.ok(cartService.getCartByUser(userId));
    }

    @PutMapping("/cart/item/update")
    public  CartDto updateQuantity(@RequestBody UpdateCartDto dto){
        return cartService.updateItemQuantity(dto);
    }

    @DeleteMapping("/cart/item/delete/{cartItemId}")
    public void deleteItem(@PathVariable Long cartItemId){
        cartService.deleteCartItem(cartItemId);
    }


}
