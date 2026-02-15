package com.example.AgriProject.repository;

import com.example.AgriProject.Enumerations.OrderStatus;
import com.example.AgriProject.entity.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByUserId(Long userId);

    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    List<Order> findByStatusAndPrintedFalse(OrderStatus orderStatus);

    @Query("""
    SELECT o FROM Order o
    LEFT JOIN FETCH o.items
    WHERE o.id = :id
    """)
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @EntityGraph(attributePaths = {"items", "user", "address"})
    List<Order> findAllByOrderDateBetweenOrderByOrderDateDesc(LocalDateTime start, LocalDateTime end);

    @EntityGraph(attributePaths = {"items", "user", "address"})
    List<Order> findAllByOrderByOrderDateDesc();

    List<Order> findByPrintedFalseOrderByOrderDateDesc();

    List<Order> findByPrintedFalseAndOrderDateBetweenOrderByOrderDateDesc(
            LocalDateTime start,
            LocalDateTime end
    );

}
