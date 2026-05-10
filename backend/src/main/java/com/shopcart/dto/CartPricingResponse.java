package com.shopcart.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for cart pricing calculation responses.
 * 
 * <p>This DTO contains comprehensive pricing information for a shopping cart,
 * including all discount calculations and shipping details.</p>
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartPricingResponse {

    /**
     * Total amount of products before any discounts
     */
    private BigDecimal totalProductAmount;

    /**
     * Total amount after applying product/order discounts
     */
    private BigDecimal totalAfterProductDiscounts;

    /**
     * Base shipping fee before any shipping discounts
     */
    private BigDecimal baseShippingFee;

    /**
     * Shipping fee after applying shipping discounts
     */
    private BigDecimal finalShippingFee;

    /**
     * Total amount after all discounts (product + shipping)
     */
    private BigDecimal finalTotalAmount;

    /**
     * List of applied coupons with their discount amounts
     */
    private List<AppliedCouponDTO> appliedCoupons;

    /**
     * Selected shipping method information
     */
    private ShippingMethodDTO shippingMethod;

    /**
     * Minimum estimated delivery date
     */
    private LocalDate estimatedDeliveryMinDate;

    /**
     * Maximum estimated delivery date
     */
    private LocalDate estimatedDeliveryMaxDate;

    /**
     * DTO for applied coupon information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AppliedCouponDTO {
        private String code;
        private String discountType;
        private BigDecimal discountValue;
        private BigDecimal appliedAmount;
        private String scope; // ORDER or SHIPPING
    }

    /**
     * DTO for shipping method information
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingMethodDTO {
        private UUID id;
        private String code;
        private String name;
        private String description;
        private BigDecimal baseFee;
    }
}
