package com.shopcart.service;

import java.util.List;
import java.util.UUID;

import com.shopcart.dto.AddAddressRequest;
import com.shopcart.dto.AddressResponse;
import com.shopcart.dto.UpdateAddressRequest;

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
    
    /**
     * Update an existing address for a specific user
     * @param userId the ID of the user
     * @param addressId the ID of the address to update
     * @param updateAddressRequest the address update data
     * @return the updated address response
     */
    AddressResponse updateAddress(UUID userId, UUID addressId, UpdateAddressRequest updateAddressRequest);
}