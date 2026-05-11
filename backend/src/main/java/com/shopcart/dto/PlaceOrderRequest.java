package com.shopcart.dto;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for placing order requests.
 *
 * <p>This DTO validates and encapsulates the data required to place an order,
 * including shipping information, payment method, and applied coupons.</p>
 *
 * <p><b>Validation Rules:</b>
 * <ul>
 *   <li>Address ID is required and must be a valid UUID</li>
 *   <li>Shipping method ID is required and must be a valid UUID</li>
 *   <li>Payment method ID is required and must be a valid UUID</li>
 *   <li>Coupon IDs list can be empty but cannot contain null values</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Data
public class PlaceOrderRequest {
    
    /**
     * Unique identifier of the shipping address to use for this order.
     *
     * <p>Required field - must reference an existing address belonging to the user.</p>
     */
    @NotNull(message = "Address ID is required")
    private UUID addressId;
    
    /**
     * Unique identifier of the shipping method to use for this order.
     *
     * <p>Required field - must reference an existing active shipping method.</p>
     */
    @NotNull(message = "Shipping method ID is required")
    private UUID shippingMethodId;
    
    /**
     * Unique identifier of the payment method to use for this order.
     *
     * <p>Required field - must reference an existing active payment method.</p>
     */
    @NotNull(message = "Payment method ID is required")
    private UUID paymentMethodId;
    
    /**
     * Full name of the recipient for shipping.
     *
     * <p>Required field - full name for delivery.</p>
     */
    @NotNull(message = "Shipping full name is required")
    private String shippingFullName;
    
    /**
     * Phone number of the recipient for shipping.
     *
     * <p>Required field - phone number for delivery contact.</p>
     */
    @NotNull(message = "Shipping phone is required")
    private String shippingPhone;
    
    /**
     * List of coupon IDs to apply to this order.
     *
     * <p>Optional field - can be empty if no coupons are applied.
     * Each coupon ID will be validated for eligibility and usage limits.</p>
     */
    @NotNull(message = "Coupon list cannot contain null values")
    private List<UUID> couponIds;
    
    /**
     * Default constructor.
     */
    public PlaceOrderRequest() {
    }
    
    /**
     * Parameterized constructor for creating place order requests.
     *
     * @param addressId The UUID of the shipping address
     * @param shippingMethodId The UUID of the shipping method
     * @param paymentMethodId The UUID of the payment method
     * @param shippingFullName Full name of the recipient for shipping
     * @param shippingPhone Phone number of the recipient for shipping
     * @param couponIds List of coupon IDs to apply (can be empty)
     */
    public PlaceOrderRequest(UUID addressId, UUID shippingMethodId, UUID paymentMethodId, 
                           String shippingFullName, String shippingPhone, List<UUID> couponIds) {
        this.addressId = addressId;
        this.shippingMethodId = shippingMethodId;
        this.paymentMethodId = paymentMethodId;
        this.shippingFullName = shippingFullName;
        this.shippingPhone = shippingPhone;
        this.couponIds = couponIds;
    }
}
