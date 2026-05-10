package com.shopcart.exception;

/**
 * Base exception for coupon-related errors.
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
public class CouponException extends RuntimeException {
    
    public CouponException(String message) {
        super(message);
    }
    
    public CouponException(String message, Throwable cause) {
        super(message, cause);
    }
}
