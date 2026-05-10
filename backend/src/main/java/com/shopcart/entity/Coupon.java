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

/**
 * Entity representing the coupons table in database
 * Stores discount coupons that can be applied to orders or shipping
 */
@Entity
@Data
@Table(name = "coupons")
@AllArgsConstructor
public class Coupon {

    /**
     * Primary key for the coupon
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Unique coupon code
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Discount value (fixed amount or percentage)
     */
    @Column(name = "discount_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal discountValue;

    /**
     * Expiry date of the coupon
     */
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;

    /**
     * Minimum order value required to use the coupon
     */
    @Column(name = "min_order_value", nullable = false, precision = 19, scale = 2)
    private BigDecimal minOrderValue;

    /**
     * Maximum discount amount (for percentage coupons)
     */
    @Column(name = "max_discount", precision = 19, scale = 2)
    private BigDecimal maxDiscount;

    /**
     * Type of discount: FIXED or PERCENT
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    private DiscountType discountType;

    /**
     * Start date when coupon becomes active
     */
    @Column(name = "start_date")
    private LocalDateTime startDate;

    /**
     * Number of times a user can use this coupon
     */
    @Column(name = "usage_per_user", nullable = false)
    private Integer usagePerUser;

    /**
     * Status of the coupon: ACTIVE or INACTIVE
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private CouponStatus status;

    /**
     * Timestamp when the coupon was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Scope of the coupon: ORDER or SHIPPING
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "coupon_scope", nullable = false)
    private CouponScope couponScope;

    /**
     * Default constructor that initializes creation timestamp and default values
     */
    public Coupon() {
        this.createdAt = LocalDateTime.now();
        this.minOrderValue = BigDecimal.ZERO;
        this.usagePerUser = 1;
        this.status = CouponStatus.ACTIVE;
        this.couponScope = CouponScope.ORDER;
    }

    /**
     * Enum for discount types
     */
    public enum DiscountType {
        FIXED,
        PERCENT
    }

    /**
     * Enum for coupon statuses
     */
    public enum CouponStatus {
        ACTIVE,
        INACTIVE
    }

    /**
     * Enum for coupon scopes
     */
    public enum CouponScope {
        ORDER,
        SHIPPING
    }
}
