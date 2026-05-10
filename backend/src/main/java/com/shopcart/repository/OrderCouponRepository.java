package com.shopcart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.OrderCoupon;

/**
 * Repository interface for OrderCoupon entity operations.
 * 
 * <p>This repository provides database access methods for order coupon management,
 * including usage tracking and validation.</p>
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Repository
public interface OrderCouponRepository extends JpaRepository<OrderCoupon, UUID> {

    /**
     * Counts how many times a specific user has used a specific coupon.
     * 
     * <p>This native SQL query joins order_coupons with orders to get the user_id and
     * counts the total usage of a coupon by a specific user.</p>
     *
     * @param couponId The coupon ID to check usage for
     * @param userId The user ID to check usage for
     * @return The number of times the user has used the coupon
     */
    @Query(value = "SELECT COUNT(oc.order_coupon_id) FROM order_coupons oc " +
                   "JOIN orders o ON oc.order_id = o.id " +
                   "WHERE oc.coupon_id = :couponId AND o.user_id = :userId", 
           nativeQuery = true)
    long countCouponUsageByUser(@Param("couponId") UUID couponId, 
                                @Param("userId") UUID userId);

    /**
     * Finds all coupons applied to a specific order.
     *
     * @param orderId The order ID to search for
     * @return List of order coupons for the specified order
     */
    @Query("SELECT oc FROM OrderCoupon oc WHERE oc.orderId = :orderId")
    List<OrderCoupon> findByOrderId(@Param("orderId") UUID orderId);
}
