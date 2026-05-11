package com.shopcart.exception;

/**
 * Exception thrown when coupon validation fails.
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
public class CouponValidationException extends CouponException {
    
    public CouponValidationException(String message) {
        super(message);
    }
    
    public static class InactiveCouponException extends CouponValidationException {
        public InactiveCouponException(String couponCode) {
            super("Coupon '" + couponCode + "' is not active");
        }
    }
    
    public static class CouponNotStartedException extends CouponValidationException {
        public CouponNotStartedException(String couponCode) {
            super("Coupon '" + couponCode + "' is not yet valid");
        }
    }
    
    public static class ExpiredCouponException extends CouponValidationException {
        public ExpiredCouponException(String couponCode) {
            super("Coupon '" + couponCode + "' has expired");
        }
    }
    
    public static class MinimumOrderValueException extends CouponValidationException {
        public MinimumOrderValueException(String couponCode, String minOrderValue) {
            super("Coupon '" + couponCode + "' requires minimum order value of " + minOrderValue);
        }
    }
    
    public static class UsageLimitExceededException extends CouponValidationException {
        public UsageLimitExceededException(String couponCode) {
            super("You have exceeded the usage limit for coupon '" + couponCode + "'");
        }
    }
}
