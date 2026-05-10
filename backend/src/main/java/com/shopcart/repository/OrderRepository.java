package com.shopcart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.Order;

/**
 * Repository interface for Order entity operations.
 * 
 * <p>This repository provides database access methods for order management,
 * including finding orders by user and status tracking.</p>
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    /**
     * Finds all orders for a specific user.
     *
     * @param userId The user ID to search for
     * @return List of orders belonging to the user
     */
    List<Order> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
