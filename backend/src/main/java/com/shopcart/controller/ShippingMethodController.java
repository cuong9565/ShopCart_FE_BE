package com.shopcart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.dto.ShippingMethodResponse;
import com.shopcart.service.ShippingMethodService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for ShippingMethod operations
 * Provides endpoints for shipping method management without authentication requirement
 */
@RestController
@RequestMapping("/api/shipping-methods")
@RequiredArgsConstructor
public class ShippingMethodController {

    private final ShippingMethodService shippingMethodService;

    /**
     * Get all active shipping methods
     * This endpoint does not require authentication
     * @return ResponseEntity containing list of active shipping methods
     */
    @GetMapping
    public ResponseEntity<List<ShippingMethodResponse>> getAllActiveShippingMethods() {
        List<ShippingMethodResponse> shippingMethods = shippingMethodService.getAllActiveShippingMethods();
        return ResponseEntity.ok(shippingMethods);
    }

    /**
     * Get a shipping method by its ID
     * This endpoint does not require authentication
     * @param id the ID of the shipping method
     * @return ResponseEntity containing the shipping method
     */
    @GetMapping("/{id}")
    public ResponseEntity<ShippingMethodResponse> getShippingMethodById(@PathVariable String id) {
        ShippingMethodResponse shippingMethod = shippingMethodService.getShippingMethodById(id);
        return ResponseEntity.ok(shippingMethod);
    }
}
