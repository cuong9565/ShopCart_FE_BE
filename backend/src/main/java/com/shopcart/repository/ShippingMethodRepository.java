package com.shopcart.repository;

import java.util.List;
import java.util.Optional;
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
     * Find a shipping method by its code
     * @param code the shipping method code
     * @return optional containing the shipping method if found
     */
    Optional<ShippingMethod> findByCode(String code);
    
    /**
     * Find all active shipping methods
     * @return list of active shipping methods
     */
    List<ShippingMethod> findByIsActiveTrue();
    
    /**
     * Check if a shipping method exists by its code
     * @param code the shipping method code
     * @return true if shipping method exists, false otherwise
     */
    boolean existsByCode(String code);
}
