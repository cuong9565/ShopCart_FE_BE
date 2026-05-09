package com.shopcart.service;

import java.util.List;
import java.util.UUID;

import com.shopcart.dto.AddAddressRequest;
import com.shopcart.dto.AddressResponse;

/**
 * Service interface for Address operations
 * Provides business logic for address management
 */
public interface AddressService {
    /**
     * Create a new address for a specific user
     * @param userId the ID of the user creating the address
     * @param addAddressRequest the address request data to create
     * @return the created address response
     */
    AddressResponse createAddress(UUID userId, AddAddressRequest addAddressRequest);
    
    /**
     * Get all addresses for a specific user
     * @param userId the ID of the user
     * @return list of addresses belonging to the user
     */
    List<AddressResponse> getAddressesByUserId(UUID userId);
}