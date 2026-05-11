package com.shopcart.service;

import java.util.UUID;

import com.shopcart.dto.OrderResponse;
import com.shopcart.dto.PlaceOrderRequest;

/**
 * Service interface for order operations.
 *
 * <p>This service provides business logic for managing orders,
 * including order placement, coupon validation, payment processing, and inventory management.</p>
 *
 * <p><b>Key Operations:</b>
 * <ul>
 *   <li>Place orders with comprehensive validation and processing</li>
 *   <li>Validate and apply coupons with usage limit checking</li>
 *   <li>Process payments and create payment records</li>
 *   <li>Manage inventory updates during order processing</li>
 *   <li>Retrieve order details and user order history</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
public interface OrderService {

    /**
     * Places a new order for a user with comprehensive validation and processing.
     *
     * <p>This method performs the complete order placement process:
     * <ul>
     *   <li>Validates user, address, shipping method, and payment method</li>
     *   <li>Validates each coupon for eligibility and usage limits</li>
     *   <li>Calculates pricing including discounts and shipping fees</li>
     *   <li>Creates order, order items, order coupons, and order payment records</li>
     *   <li>Updates inventory to reflect purchased quantities</li>
     *   <li>Clears user's cart after successful order placement</li>
     * </ul>
     * </p>
     *
     * @param userId The UUID of the user placing the order
     * @param request The order placement request with all necessary details
     * @return Complete order response with all order details
     * @throws IllegalArgumentException if validation fails for any component
     * @throws IllegalStateException if inventory is insufficient or other business rule violations
     * @throws com.shopcart.exception.CouponException if coupon validation fails
     */
    OrderResponse placeOrder(UUID userId, PlaceOrderRequest request);
    
    /**
     * Validates a coupon for a specific user and order value.
     *
     * <p>This method checks:
     * <ul>
     *   <li>Coupon exists and is active</li>
     *   <li>Coupon is within valid date range</li>
     *   <li>User hasn't exceeded usage limits</li>
     *   <li>Order value meets minimum requirements</li>
     * </ul>
     * </p>
     *
     * @param couponId The UUID of the coupon to validate
     * @param userId The UUID of the user attempting to use the coupon
     * @param orderValue The total order value for validation
     * @return The calculated discount amount
     * @throws com.shopcart.exception.CouponException if coupon validation fails
     */
    java.math.BigDecimal validateAndCalculateCouponDiscount(UUID couponId, UUID userId, java.math.BigDecimal orderValue);

    /**
     * Updates inventory quantities for purchased items.
     *
     * <p>This method reduces inventory quantities for all items in an order.
     * Throws exception if any item has insufficient inventory.</p>
     *
     * @param userId The UUID of the user placing the order
     * @throws IllegalStateException if any item has insufficient inventory
     */
    void updateInventoryForOrder(UUID userId);

    /**
     * Clears the user's shopping cart after successful order placement.
     *
     * @param userId The UUID of the user whose cart to clear
     * @return The number of cart items that were removed
     */
    int clearUserCart(UUID userId);
}
