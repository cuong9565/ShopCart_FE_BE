package com.shopcart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.OrderItem;

/**
 * Repository interface for OrderItem entity operations.
 * 
 * <p>This repository provides database access methods for order item management,
 * including retrieving items for specific orders.</p>
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    /**
     * Finds all order items for a specific order.
     *
     * @param orderId The order ID to search for
     * @return List of order items belonging to the order
     */
    List<OrderItem> findByOrderId(UUID orderId);
}
