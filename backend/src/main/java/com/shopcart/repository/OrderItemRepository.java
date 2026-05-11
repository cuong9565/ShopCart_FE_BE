package com.shopcart.repository;

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
    // No additional methods needed, JpaRepository provides all standard CRUD operations
}
