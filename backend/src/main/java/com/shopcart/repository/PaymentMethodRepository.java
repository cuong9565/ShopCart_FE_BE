package com.shopcart.repository;

import java.util.List;
import java.util.Optional;
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
     * Find a payment method by its code
     * @param code the payment method code
     * @return optional containing the payment method if found
     */
    Optional<PaymentMethod> findByCode(String code);
    
    /**
     * Find all active payment methods
     * @return list of active payment methods
     */
    List<PaymentMethod> findByIsActiveTrue();
    
    /**
     * Check if a payment method exists by its code
     * @param code the payment method code
     * @return true if payment method exists, false otherwise
     */
    boolean existsByCode(String code);
}
