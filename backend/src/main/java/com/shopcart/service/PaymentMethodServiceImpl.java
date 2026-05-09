package com.shopcart.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopcart.dto.PaymentMethodResponse;
import com.shopcart.entity.PaymentMethod;
import com.shopcart.repository.PaymentMethodRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for PaymentMethod operations
 * Handles business logic for payment method management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentMethodServiceImpl implements PaymentMethodService {

    private final PaymentMethodRepository paymentMethodRepository;

    /**
     * {@inheritDoc}
     * Retrieves all active payment methods
     */
    @Override
    public List<PaymentMethodResponse> getAllActivePaymentMethods() {
        return paymentMethodRepository.findByIsActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Retrieves a payment method by its ID
     */
    @Override
    public PaymentMethodResponse getPaymentMethodById(String id) {
        PaymentMethod paymentMethod = paymentMethodRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Payment method not found with ID: " + id));
        return convertToResponse(paymentMethod);
    }

    /**
     * Helper method to convert PaymentMethod entity to PaymentMethodResponse DTO
     * @param paymentMethod the entity to convert
     * @return the response DTO
     */
    private PaymentMethodResponse convertToResponse(PaymentMethod paymentMethod) {
        return PaymentMethodResponse.builder()
                .id(paymentMethod.getId())
                .code(paymentMethod.getCode())
                .name(paymentMethod.getName())
                .isActive(paymentMethod.getIsActive())
                .createdAt(paymentMethod.getCreatedAt())
                .build();
    }
}
