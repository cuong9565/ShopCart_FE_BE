package com.shopcart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for order responses.
 *
 * <p>This DTO encapsulates order information returned to the client after
 * successful order placement, including order details, items, and pricing.</p>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    
    /**
     * Unique identifier of the order.
     */
    private UUID id;
    
    /**
     * Order status.
     */
    private String status;
    
    /**
     * Shipping information.
     */
    private ShippingInfo shippingInfo;
    
    /**
     * Payment information.
     */
    private PaymentInfo paymentInfo;
    
    /**
     * Order items.
     */
    private List<OrderItemResponse> items;
    
    /**
     * Pricing information.
     */
    private PricingInfo pricingInfo;
    
    /**
     * Timestamps.
     */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * Applied coupons information.
     */
    private List<CouponInfo> appliedCoupons;
    
    /**
     * Nested class for shipping information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShippingInfo {
        private String fullName;
        private String phone;
        private String addressLine;
        private String city;
        private String district;
        private String ward;
        private String methodName;
        private BigDecimal shippingFee;
        private Integer estimatedDeliveryMin;
        private Integer estimatedDeliveryMax;
    }
    
    /**
     * Nested class for payment information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentInfo {
        private String methodName;
        private String status;
        private LocalDateTime paidAt;
    }
    
    /**
     * Nested class for order item information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse {
        private UUID productId;
        private String productName;
        private Integer quantity;
        private BigDecimal price;
        private BigDecimal totalPrice;
    }
    
    /**
     * Nested class for pricing information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PricingInfo {
        private BigDecimal subtotal;
        private BigDecimal shippingFee;
        private BigDecimal discount;
        private BigDecimal couponDiscount;
        private BigDecimal finalPrice;
    }
    
    /**
     * Nested class for coupon information.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CouponInfo {
        private UUID couponId;
        private String code;
        private BigDecimal discountAmount;
    }
}
