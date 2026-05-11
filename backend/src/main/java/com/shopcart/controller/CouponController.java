package com.shopcart.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.dto.ValidCouponDTO;
import com.shopcart.security.CustomUserDetails;
import com.shopcart.service.CouponService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * REST controller for coupon operations.
 *
 * <p>This controller provides endpoints for managing coupons,
 * including retrieving valid coupons for users and validating coupon usage.</p>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Slf4j
public class CouponController {

    private final CouponService couponService;

    /**
     * Gets list of valid coupons for the authenticated user.
     *
     * <p>This endpoint returns all active coupons that the user can use,
     * including remaining usage count and applicability to current cart.</p>
     *
     * @return List of valid coupons with usage information
     */
    @GetMapping("/valid")
    public ResponseEntity<List<ValidCouponDTO>> getValidCoupons(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        List<ValidCouponDTO> validCoupons = couponService.getValidCouponsForUser(userId);
        return ResponseEntity.ok(validCoupons);
    }
}
