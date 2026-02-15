package com.example.AgriProject.repository;

import com.example.AgriProject.entity.Cart;
import com.example.AgriProject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Optional<Cart> findByUser(User user);

    Optional<Cart> findByUserId(Long userId);

    @Modifying
    @Query("""
    UPDATE Cart c
    SET c.totalAmount = COALESCE((
        SELECT SUM(ci.subtotal)
        FROM CartItem ci
        WHERE ci.cart.id = c.id
    ), 0)
    WHERE EXISTS (
        SELECT 1 FROM CartItem ci WHERE ci.product.id = :productId AND ci.cart.id = c.id
    )
""")
    void recalculateTotalForCartsContainingProduct(@Param("productId") Long productId);

}
