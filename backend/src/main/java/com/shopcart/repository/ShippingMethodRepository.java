package com.shopcart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.ShippingMethod;

/**
 * Repository interface for ShippingMethod entity operations
 * Provides database access methods for shipping method management
 */
@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, UUID> {
    
    /**
     * Find all active shipping methods
     * @return list of active shipping methods
     */
    List<ShippingMethod> findByIsActiveTrue();
}
