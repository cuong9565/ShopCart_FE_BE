package com.shopcart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the orders table in database
 * Stores order information including shipping details, pricing, and status
 */
@Entity
@Data
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    /**
     * Primary key for the order
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * User who placed the order
     */
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    /**
     * Recipient's full name for shipping
     */
    @Column(name = "shipping_full_name", nullable = false, length = 255)
    private String shippingFullName;

    /**
     * Recipient's phone number for shipping
     */
    @Column(name = "shipping_phone", nullable = false, length = 20)
    private String shippingPhone;

    /**
     * Detailed shipping address
     */
    @Column(name = "shipping_address_line", nullable = false, columnDefinition = "TEXT")
    private String shippingAddressLine;

    /**
     * Shipping city
     */
    @Column(name = "shipping_city", nullable = false, length = 255)
    private String shippingCity;

    /**
     * Shipping district
     */
    @Column(name = "shipping_district", nullable = false, length = 255)
    private String shippingDistrict;

    /**
     * Shipping ward
     */
    @Column(name = "shipping_ward", nullable = false, length = 255)
    private String shippingWard;

    /**
     * Shipping method ID
     */
    @Column(name = "shipping_method_id")
    private UUID shippingMethodId;

    /**
     * Shipping method name snapshot
     */
    @Column(name = "shipping_method_name", length = 255)
    private String shippingMethodName;

    /**
     * Shipping fee
     */
    @Column(name = "shipping_fee", precision = 19, scale = 2)
    private BigDecimal shippingFee;

    /**
     * Minimum estimated delivery days
     */
    @Column(name = "estimated_delivery_min")
    private Integer estimatedDeliveryMin;

    /**
     * Maximum estimated delivery days
     */
    @Column(name = "estimated_delivery_max")
    private Integer estimatedDeliveryMax;

    /**
     * Subtotal (total price of items before discounts)
     */
    @Column(name = "subtotal", nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;

    /**
     * Total discount amount
     */
    @Column(name = "discount", precision = 19, scale = 2)
    private BigDecimal discount;

    /**
     * Final price that user needs to pay
     */
    @Column(name = "final_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal finalPrice;

    /**
     * Coupon code snapshot
     */
    @Column(name = "coupon_code", length = 50)
    private String couponCode;

    /**
     * Coupon discount amount
     */
    @Column(name = "coupon_discount_amount", precision = 19, scale = 2)
    private BigDecimal couponDiscountAmount;

    /**
     * Order status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private OrderStatus status;

    /**
     * Payment expiration time
     */
    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

    /**
     * Order creation timestamp
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Last update timestamp
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Original address ID used for this order
     */
    @Column(name = "address_id", nullable = false)
    private UUID addressId;

    /**
     * Initializes default values for the order
     */
    public void initializeDefaults() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.shippingFee = BigDecimal.ZERO;
        this.discount = BigDecimal.ZERO;
        this.couponDiscountAmount = BigDecimal.ZERO;
        this.status = OrderStatus.PENDING;
    }

    /**
     * Enum for order statuses
     */
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPING,
        DELIVERED,
        CANCELLED,
        FAILED
    }
}
