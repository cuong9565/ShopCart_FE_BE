package com.shopcart.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopcart.dto.ValidCouponDTO;
import com.shopcart.entity.CartItem;
import com.shopcart.entity.Coupon;
import com.shopcart.repository.CartItemRepository;
import com.shopcart.repository.CouponRepository;
import com.shopcart.repository.OrderCouponRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for coupon operations.
 *
 * <p>This service provides business logic for managing coupons,
 * including validation, usage tracking, and retrieving available coupons for users.</p>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CouponServiceImpl implements CouponService {
    
    private final CouponRepository couponRepository;
    private final OrderCouponRepository orderCouponRepository;
    private final CartItemRepository cartItemRepository;

    @Override
    @Transactional(readOnly = true)
    public List<ValidCouponDTO> getValidCouponsForUser(UUID userId) {
        LocalDateTime now = LocalDateTime.now();
        
        // Get user's cart and calculate total
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        BigDecimal currentCartValue = cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Get all active coupons that are currently valid
        List<Coupon> activeCoupons = couponRepository.findActiveCoupons(now);
        
        return activeCoupons.stream()
                .map(coupon -> {
                    // Check usage limit for this user
                    long usageCount = orderCouponRepository.countCouponUsageByUser(coupon.getId(), userId);
                    int remainingUsage = Math.max(0, coupon.getUsagePerUser() - (int) usageCount);
                    
                    // Check if applicable to current cart value
                    boolean applicableToCurrentCart = currentCartValue.compareTo(coupon.getMinOrderValue()) >= 0;
                    
                    return ValidCouponDTO.fromEntity(coupon, remainingUsage, applicableToCurrentCart);
                })
                .filter(dto -> dto.getRemainingUsage() > 0) // Only return coupons with remaining usage
                .toList();
    }
}
