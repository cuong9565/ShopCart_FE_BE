package com.shopcart.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Address responses
 * Contains address information without exposing internal entity structure
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressResponse {
    /**
     * Unique identifier for the address
     */
    private UUID id;
    
    /**
     * Full address line including street number and name
     */
    private String addressLine;
    
    /**
     * City name
     */
    private String city;
    
    /**
     * District/County name
     */
    private String district;
    
    /**
     * Ward/Commune name
     */
    private String ward;
    
    /**
     * Flag indicating if this is the default address
     */
    private Boolean isDefault;
    
    /**
     * ID of the user who owns this address
     */
    private UUID userId;
}