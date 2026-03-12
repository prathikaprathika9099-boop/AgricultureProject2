package com.example.AgriProject.service;

import com.example.AgriProject.dto.CartDto;
import com.example.AgriProject.dto.CartItemDto;
import com.example.AgriProject.dto.CartMapper;
import com.example.AgriProject.dto.UpdateCartDto;
import com.example.AgriProject.entity.Cart;
import com.example.AgriProject.entity.CartItem;
import com.example.AgriProject.entity.Product;
import com.example.AgriProject.entity.User;
import com.example.AgriProject.repository.CartItemRepository;
import com.example.AgriProject.repository.CartRepository;
import com.example.AgriProject.repository.ProductRepository;
import com.example.AgriProject.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartDto addProductToCart(Long userId, Long productId, int quantity){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));

        Product product=productRepository.findById(productId)
                .orElseThrow(()->new RuntimeException("Product not found"));

        if(quantity>product.getStock()){
            throw new RuntimeException("Quantity is more than stock available");
        }
        Cart cart = cartRepository.findByUser(user).orElse(null);

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setTotalAmount(0.0);
            cart.setItems(new ArrayList<>());
            cart = cartRepository.save(cart);
        }



        CartItem cartItem=cartItemRepository
                .findByCartAndProduct(cart,product)
                .orElse(null);

        if(cartItem!=null){
            int finalQuantity=cartItem.getQuantity()+quantity;

            if(finalQuantity>product.getStock()) {
                throw new RuntimeException("Quantity is more than stock available");
            }
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        }
        else{
            cartItem=CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .price(product.getCost())
                    .build();
            cart.getItems().add(cartItem);
        }

        double total = cart.getItems()
                .stream()
                .mapToDouble(item -> item.getPrice() * item.getQuantity())
                .sum();

        cart.setTotalAmount(total);

        Cart savedCart= cartRepository.save(cart);
        return CartMapper.toCartDto(savedCart);
    }

    public CartDto getCartByUser(Long userId){
        User user=userRepository.findById(userId)
                .orElseThrow(()->new RuntimeException("User not found"));

        Cart cart= cartRepository.findByUser(user)
                .orElseThrow(()->new RuntimeException("Cart is empty"));

        CartDto cartDto = CartMapper.toCartDto(cart);

        for(CartItemDto itemDto : cartDto.getItems()){
            Product freshProduct = productRepository
                    .findById(itemDto.getProduct().getId())
                    .orElseThrow(()->new RuntimeException("Product not found"));

            itemDto.setStock(freshProduct.getStock());
        }

        return cartDto;
    }

    @Transactional
    public void clearCart(Long id) {
        User user=userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("User not found"));

        Cart cart=cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found"));
        cart.getItems().clear();

        cart.setTotalAmount(0.0);
        cartRepository.save(cart);
    }

    public CartDto updateItemQuantity(UpdateCartDto dto) {
        CartItem item=cartItemRepository.findById(dto.getCartItemId())
                .orElseThrow(()->new RuntimeException("Item not found"));

        int newQty= item.getQuantity()+dto.getDelta();

        if(newQty<=0){
            cartItemRepository.delete(item);
        }
        else{
            item.setQuantity(newQty);
        }

        Cart cart=item.getCart();

        double total=cart.getItems().stream()
                .mapToDouble(i->i.getPrice()*i.getQuantity())
                .sum();
        cart.setTotalAmount(total);
        cartRepository.save(cart);

        return CartMapper.toCartDto(cart);
    }

    public void deleteCartItem(Long cartItemId) {
        CartItem item=cartItemRepository.findById(cartItemId)
                .orElseThrow(()->new RuntimeException("Item not found"));

        Cart cart=item.getCart();

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        double total=cart.getItems().stream()
                .mapToDouble(i->i.getQuantity()*i.getPrice())
                .sum();

        cart.setTotalAmount(total);
        cartRepository.save(cart);
    }

}
