package com.example.AgriProject.repository;

import com.example.AgriProject.entity.Cart;
import com.example.AgriProject.entity.CartItem;
import com.example.AgriProject.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    void deleteByProductId(Long id);



    @Modifying
    @Query("""
        UPDATE CartItem c
        SET c.price = :newPrice,
            c.subtotal = :newPrice * c.quantity
        WHERE c.product.id = :productId
    """)
    void updateCartItemsPrice(@Param("productId") Long productId,
                             @Param("newPrice") double newPrice);

    @Modifying
    @Query("""
        UPDATE CartItem c
        SET c.quantity = :newStock,
            c.subtotal = c.price * :newStock
        WHERE c.product.id = :productId
          AND c.quantity > :newStock
    """)
    void reduceCartItemsQuantityIfStockLess(@Param("productId") Long productId,
                                           @Param("newStock") int newStock);

    @Modifying
    @Query("""
        DELETE FROM CartItem c
        WHERE c.product.id = :productId
          AND :newStock = 0
    """)
    void deleteCartItemsIfStockZero(@Param("productId") Long productId,
                                   @Param("newStock") int newStock);
}
