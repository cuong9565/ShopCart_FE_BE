package com.shopcart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.PaymentMethod;

/**
 * Repository interface for PaymentMethod entity operations
 * Provides database access methods for payment method management
 */
@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, UUID> {
    
    /**
     * Find all active payment methods
     * @return list of active payment methods
     */
    List<PaymentMethod> findByIsActiveTrue();
}
