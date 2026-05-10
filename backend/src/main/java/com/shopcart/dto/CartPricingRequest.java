package com.shopcart.dto;

import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for cart pricing calculation requests.
 * 
 * <p>This DTO contains all the information needed to calculate the final pricing
 * for a shopping cart, including applied coupons and shipping method.</p>
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartPricingRequest {

    /**
     * List of coupon codes to apply to the cart
     */
    private List<String> couponCodes;

    /**
     * UUID of the selected shipping method
     */
    private UUID shippingMethodId;
}
