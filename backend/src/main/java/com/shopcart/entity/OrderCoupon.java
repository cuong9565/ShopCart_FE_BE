package com.shopcart.entity;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the order_coupons table in database
 * Stores the relationship between orders and applied coupons
 */
@Entity
@Data
@Table(name = "order_coupons")
@NoArgsConstructor
@AllArgsConstructor
public class OrderCoupon {

    /**
     * Primary key for the order coupon mapping
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID orderCouponId;

    /**
     * The order ID that the coupon was applied to
     */
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    /**
     * The coupon ID that was applied
     */
    @Column(name = "coupon_id", nullable = false)
    private UUID couponId;

    /**
     * The actual discount amount applied
     */
    @Column(name = "applied_amount", precision = 19, scale = 2)
    private BigDecimal appliedAmount;
}
