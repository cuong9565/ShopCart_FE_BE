package com.shopcart.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.OrderPayment;

/**
 * Repository interface for OrderPayment entity operations.
 * 
 * <p>This repository provides database access methods for order payment management,
 * including finding payments by order and status tracking.</p>
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Repository
public interface OrderPaymentRepository extends JpaRepository<OrderPayment, UUID> {
    // No additional methods needed, JpaRepository provides all standard CRUD operations
}
