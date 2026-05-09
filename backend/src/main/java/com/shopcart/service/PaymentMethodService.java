package com.shopcart.service;

import java.util.List;

import com.shopcart.dto.PaymentMethodResponse;

/**
 * Service interface for PaymentMethod operations
 * Provides business logic for payment method management
 */
public interface PaymentMethodService {
    
    /**
     * Get all active payment methods
     * @return list of active payment methods
     */
    List<PaymentMethodResponse> getAllActivePaymentMethods();
    
    /**
     * Get a payment method by its ID
     * @param id the ID of the payment method
     * @return the payment method response
     */
    PaymentMethodResponse getPaymentMethodById(String id);
}
