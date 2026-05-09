package com.shopcart.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.dto.AddAddressRequest;
import com.shopcart.dto.AddressResponse;
import com.shopcart.dto.UpdateAddressRequest;
import com.shopcart.security.CustomUserDetails;
import com.shopcart.service.AddressService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Address operations
 * Provides endpoints for address management with user-based access control
 */
@RestController
@RequestMapping("/api/address")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * Get all addresses for the currently authenticated user
     * @return ResponseEntity containing list of user's addresses
     */
    @GetMapping
    public ResponseEntity<List<AddressResponse>> getUserAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        List<AddressResponse> addresses = addressService.getAddressesByUserId(userId);
        return ResponseEntity.ok(addresses);
    }

    /**
     * Add a new address for the currently authenticated user
     * @param addAddressRequest the address request data
     * @param userDetails the authenticated user details
     * @return ResponseEntity containing the created address
     */
    @PostMapping
    public ResponseEntity<AddressResponse> addAddress(
            @Valid @RequestBody AddAddressRequest addAddressRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        AddressResponse createdAddress = addressService.createAddress(userId, addAddressRequest);
        return ResponseEntity.ok(createdAddress);
    }

    /**
     * Update an existing address for the currently authenticated user
     * @param addressId the ID of the address to update
     * @param updateAddressRequest the address update data
     * @param userDetails the authenticated user details
     * @return ResponseEntity containing the updated address
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponse> updateAddress(
            @PathVariable UUID addressId,
            @Valid @RequestBody UpdateAddressRequest updateAddressRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        AddressResponse updatedAddress = addressService.updateAddress(userId, addressId, updateAddressRequest);
        return ResponseEntity.ok(updatedAddress);
    }
}