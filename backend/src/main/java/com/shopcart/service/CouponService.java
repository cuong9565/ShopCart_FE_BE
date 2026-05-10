package com.shopcart.service;

import java.util.List;
import java.util.UUID;

import com.shopcart.dto.ValidCouponDTO;

/**
 * Service interface for coupon operations.
 *
 * <p>This service provides business logic for managing coupons,
 * including validation, usage tracking, and retrieving available coupons for users.</p>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
public interface CouponService {

    /**
     * Gets list of valid coupons for a specific user.
     *
     * <p>This method returns all active coupons that the user can use,
     * including remaining usage count and whether they can be applied to current cart.</p>
     *
     * @param userId The UUID of the user
     * @return List of valid coupons with usage information
     */
    List<ValidCouponDTO> getValidCouponsForUser(UUID userId);
}
