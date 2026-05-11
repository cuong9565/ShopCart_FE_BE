package com.shopcart.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.dto.PaymentMethodResponse;
import com.shopcart.service.PaymentMethodService;

import lombok.RequiredArgsConstructor;

/**
 * REST Controller for PaymentMethod operations
 * Provides endpoints for payment method management without authentication requirement
 */
@RestController
@RequestMapping("/api/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;

    /**
     * Get all active payment methods
     * This endpoint does not require authentication
     * @return ResponseEntity containing list of active payment methods
     */
    @GetMapping
    public ResponseEntity<List<PaymentMethodResponse>> getAllActivePaymentMethods() {
        List<PaymentMethodResponse> paymentMethods = paymentMethodService.getAllActivePaymentMethods();
        return ResponseEntity.ok(paymentMethods);
    }

    /**
     * Get a payment method by its ID
     * This endpoint does not require authentication
     * @param id the ID of the payment method
     * @return ResponseEntity containing the payment method
     */
    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodResponse> getPaymentMethodById(@PathVariable String id) {
        PaymentMethodResponse paymentMethod = paymentMethodService.getPaymentMethodById(id);
        return ResponseEntity.ok(paymentMethod);
    }
}
