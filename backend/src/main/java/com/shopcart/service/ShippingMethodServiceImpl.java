package com.shopcart.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopcart.dto.ShippingMethodResponse;
import com.shopcart.entity.ShippingMethod;
import com.shopcart.repository.ShippingMethodRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for ShippingMethod operations
 * Handles business logic for shipping method management
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShippingMethodServiceImpl implements ShippingMethodService {

    private final ShippingMethodRepository shippingMethodRepository;

    /**
     * {@inheritDoc}
     * Retrieves all active shipping methods
     */
    @Override
    public List<ShippingMethodResponse> getAllActiveShippingMethods() {
        return shippingMethodRepository.findByIsActiveTrue().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * Retrieves a shipping method by its ID
     */
    @Override
    public ShippingMethodResponse getShippingMethodById(String id) {
        ShippingMethod shippingMethod = shippingMethodRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Shipping method not found with ID: " + id));
        return convertToResponse(shippingMethod);
    }

    /**
     * Helper method to convert ShippingMethod entity to ShippingMethodResponse DTO
     * @param shippingMethod the entity to convert
     * @return the response DTO
     */
    private ShippingMethodResponse convertToResponse(ShippingMethod shippingMethod) {
        return ShippingMethodResponse.builder()
                .id(shippingMethod.getId())
                .code(shippingMethod.getCode())
                .name(shippingMethod.getName())
                .description(shippingMethod.getDescription())
                .baseFee(shippingMethod.getBaseFee())
                .estimatedDaysMin(shippingMethod.getEstimatedDaysMin())
                .estimatedDaysMax(shippingMethod.getEstimatedDaysMax())
                .isActive(shippingMethod.getIsActive())
                .createdAt(shippingMethod.getCreatedAt())
                .build();
    }
}
