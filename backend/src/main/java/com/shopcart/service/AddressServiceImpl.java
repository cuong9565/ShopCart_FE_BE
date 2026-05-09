package com.shopcart.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopcart.dto.AddAddressRequest;
import com.shopcart.dto.AddressResponse;
import com.shopcart.dto.UpdateAddressRequest;
import com.shopcart.entity.Address;
import com.shopcart.repository.AddressRepository;
import com.shopcart.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for Address operations
 * Handles business logic for address management
 */
@Service
@RequiredArgsConstructor
@Transactional
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    /**
     * {@inheritDoc}
     * Creates a new address for the specified user after validating user existence
     */
    @Override
    public AddressResponse createAddress(UUID userId, AddAddressRequest addAddressRequest) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Create Address entity from request
        Address address = new Address();
        address.setUserId(userId);
        address.setAddressLine(addAddressRequest.getAddressLine());
        address.setCity(addAddressRequest.getCity());
        address.setDistrict(addAddressRequest.getDistrict());
        address.setWard(addAddressRequest.getWard());
        
        // Handle default address logic
        List<Address> existingAddresses = addressRepository.findByUserId(userId);
        if (existingAddresses.isEmpty()) {
            // First address - always set as default
            address.setIsDefault(true);
        } else {
            // Not specified as default - set to false
            address.setIsDefault(false);
        }
        
        Address saved = addressRepository.save(address);

        return AddressResponse.builder()
                .id(saved.getId())
                .addressLine(saved.getAddressLine())
                .city(saved.getCity())
                .district(saved.getDistrict())
                .ward(saved.getWard())
                .isDefault(saved.getIsDefault())
                .userId(saved.getUserId())
                .build();
    }

    /**
     * {@inheritDoc}
     * Retrieves all addresses belonging to a specific user
     */
    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByUserId(UUID userId) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return addressRepository.findByUserId(userId).stream()
                .map(address -> AddressResponse.builder()
                        .id(address.getId())
                        .addressLine(address.getAddressLine())
                        .city(address.getCity())
                        .district(address.getDistrict())
                        .ward(address.getWard())
                        .isDefault(address.getIsDefault())
                        .userId(address.getUserId())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Updates an existing address for the specified user
     */
    @Override
    public AddressResponse updateAddress(UUID userId, UUID addressId, UpdateAddressRequest updateAddressRequest) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Find and validate address belongs to user
        Address address = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new RuntimeException("Address not found or does not belong to user"));

        // Update address fields
        address.setAddressLine(updateAddressRequest.getAddressLine());
        address.setCity(updateAddressRequest.getCity());
        address.setDistrict(updateAddressRequest.getDistrict());
        address.setWard(updateAddressRequest.getWard());

        // Save the updated address
        Address saved = addressRepository.save(address);

        return AddressResponse.builder()
                .id(saved.getId())
                .addressLine(saved.getAddressLine())
                .city(saved.getCity())
                .district(saved.getDistrict())
                .ward(saved.getWard())
                .isDefault(saved.getIsDefault())
                .userId(saved.getUserId())
                .build();
    }
}