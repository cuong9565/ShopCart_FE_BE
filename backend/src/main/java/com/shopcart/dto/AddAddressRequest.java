package com.shopcart.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Address creation requests
 * Contains required fields for creating a new address
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddAddressRequest {
    
    /**
     * Full address line including street number and name
     */
    @NotBlank(message = "Address line is required")
    @Size(max = 500, message = "Address line must not exceed 500 characters")
    private String addressLine;
    
    /**
     * City name
     */
    @NotBlank(message = "City is required")
    @Size(max = 255, message = "City name must not exceed 255 characters")
    private String city;
    
    /**
     * District/County name
     */
    @NotBlank(message = "District is required")
    @Size(max = 255, message = "District name must not exceed 255 characters")
    private String district;
    
    /**
     * Ward/Commune name
     */
    @NotBlank(message = "Ward is required")
    @Size(max = 255, message = "Ward name must not exceed 255 characters")
    private String ward;
}
