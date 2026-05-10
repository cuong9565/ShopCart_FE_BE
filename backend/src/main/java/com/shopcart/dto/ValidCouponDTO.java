package com.shopcart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for representing valid coupons for a user.
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ValidCouponDTO {
    
    /**
     * The coupon code
     */
    private String code;
    
    /**
     * The discount value
     */
    private BigDecimal discountValue;
    
    /**
     * The discount type (FIXED or PERCENT)
     */
    private String discountType;
    
    /**
     * The minimum order value required
     */
    private BigDecimal minOrderValue;
    
    /**
     * Maximum discount amount (for percentage coupons)
     */
    private BigDecimal maxDiscount;
    
    /**
     * The scope of the coupon (ORDER or SHIPPING)
     */
    private String scope;
    
    /**
     * When the coupon becomes active
     */
    private LocalDateTime startDate;
    
    /**
     * When the coupon expires
     */
    private LocalDateTime expiryDate;
    
    /**
     * How many times this user can still use this coupon
     */
    private Integer remainingUsage;
    
    /**
     * Whether the coupon can be applied to the current cart value
     */
    private Boolean applicableToCurrentCart;
    
    /**
     * Creates ValidCouponDTO from Coupon entity
     */
    public static ValidCouponDTO fromEntity(
            com.shopcart.entity.Coupon coupon, 
            Integer remainingUsage, 
            Boolean applicableToCurrentCart) {
        ValidCouponDTO dto = new ValidCouponDTO();
        dto.setCode(coupon.getCode());
        dto.setDiscountValue(coupon.getDiscountValue());
        dto.setDiscountType(coupon.getDiscountType().name());
        dto.setMinOrderValue(coupon.getMinOrderValue());
        dto.setMaxDiscount(coupon.getMaxDiscount());
        dto.setScope(coupon.getCouponScope().name());
        dto.setStartDate(coupon.getStartDate());
        dto.setExpiryDate(coupon.getExpiryDate());
        dto.setRemainingUsage(remainingUsage);
        dto.setApplicableToCurrentCart(applicableToCurrentCart);
        return dto;
    }
}
