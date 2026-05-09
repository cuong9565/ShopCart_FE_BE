package com.shopcart.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for PaymentMethod responses
 * Contains payment method information without exposing internal entity structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentMethodResponse {
    /**
     * Unique identifier for the payment method
     */
    private UUID id;
    
    /**
     * Unique code for the payment method
     */
    private String code;
    
    /**
     * Display name of the payment method
     */
    private String name;
    
    /**
     * Flag indicating if the payment method is active
     */
    private Boolean isActive;
    
    /**
     * Timestamp when the payment method was created
     */
    private LocalDateTime createdAt;
}
