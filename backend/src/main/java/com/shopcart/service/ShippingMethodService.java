package com.shopcart.service;

import java.util.List;

import com.shopcart.dto.ShippingMethodResponse;

/**
 * Service interface for ShippingMethod operations
 * Provides business logic for shipping method management
 */
public interface ShippingMethodService {
    
    /**
     * Get all active shipping methods
     * @return list of active shipping methods
     */
    List<ShippingMethodResponse> getAllActiveShippingMethods();
    
    /**
     * Get a shipping method by its ID
     * @param id the ID of the shipping method
     * @return the shipping method response
     */
    ShippingMethodResponse getShippingMethodById(String id);
}
