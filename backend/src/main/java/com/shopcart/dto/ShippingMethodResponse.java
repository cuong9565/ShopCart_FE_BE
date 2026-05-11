package com.shopcart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ShippingMethod responses
 * Contains shipping method information without exposing internal entity structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShippingMethodResponse {
    /**
     * Unique identifier for the shipping method
     */
    private UUID id;
    
    /**
     * Unique code for the shipping method
     */
    private String code;
    
    /**
     * Display name of the shipping method
     */
    private String name;
    
    /**
     * Description of the shipping method
     */
    private String description;
    
    /**
     * Base fee for the shipping method
     */
    private BigDecimal baseFee;
    
    /**
     * Minimum estimated delivery days
     */
    private Integer estimatedDaysMin;
    
    /**
     * Maximum estimated delivery days
     */
    private Integer estimatedDaysMax;
    
    /**
     * Flag indicating if the shipping method is active
     */
    private Boolean isActive;
    
    /**
     * Timestamp when the shipping method was created
     */
    private LocalDateTime createdAt;
}
