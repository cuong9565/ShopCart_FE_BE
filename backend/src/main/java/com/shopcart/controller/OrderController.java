package com.shopcart.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.dto.OrderResponse;
import com.shopcart.dto.PlaceOrderRequest;
import com.shopcart.security.CustomUserDetails;
import com.shopcart.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for order operations.
 *
 * <p>This controller provides endpoints for managing user orders,
 * including placing new orders, retrieving order details, and viewing order history.</p>
 *
 * <p><b>Endpoints:</b>
 * <ul>
 *   <li>POST /api/orders - Place a new order</li>
 *   <li>GET /api/orders/{orderId} - Get specific order details</li>
 *   <li>GET /api/orders - Get all orders for authenticated user</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;

    /**
     * Places a new order for the authenticated user.
     *
     * <p>This endpoint performs comprehensive order processing including:
     * <ul>
     *   <li>Validation of address, shipping method, and payment method</li>
     *   <li>Coupon validation and discount application</li>
     *   <li>Inventory checking and updates</li>
     *   <li>Order creation and cart clearing</li>
     * </ul>
     * </p>
     *
     * @param userDetails The authenticated user details
     * @param request The order placement request with all necessary details
     * @return Complete order response with all order details
     * @throws IllegalArgumentException if validation fails for any component
     * @throws IllegalStateException if inventory is insufficient or other business rule violations
     * @throws com.shopcart.exception.CouponException if coupon validation fails
     */
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody PlaceOrderRequest request) {
        
        UUID userId = userDetails.getId();
        OrderResponse orderResponse = orderService.placeOrder(userId, request);
        
        return ResponseEntity.ok(orderResponse);
    }

    /**
     * Retrieves specific order details for the authenticated user.
     *
     * <p>This endpoint returns complete order information including items,
     * pricing, shipping details, and payment status. Users can only view
     * their own orders.</p>
     *
     * @param userDetails The authenticated user details
     * @param orderId The UUID of the order to retrieve
     * @return Complete order response with all details
     * @throws IllegalArgumentException if order not found or doesn't belong to user
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable UUID orderId) {
        
        UUID userId = userDetails.getId();
        OrderResponse orderResponse = orderService.getOrderById(orderId, userId);
        
        return ResponseEntity.ok(orderResponse);
    }

    /**
     * Retrieves all orders for the authenticated user.
     *
     * <p>This endpoint returns the user's complete order history,
     * ordered by creation date (newest first). Each order includes
     * full details including items, pricing, and status.</p>
     *
     * @param userDetails The authenticated user details
     * @return List of order responses for the user
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getUserOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        
        UUID userId = userDetails.getId();
        List<OrderResponse> orders = orderService.getUserOrders(userId);
        
        return ResponseEntity.ok(orders);
    }
}
