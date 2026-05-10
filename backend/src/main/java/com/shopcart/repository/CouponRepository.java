package com.shopcart.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.Coupon;

/**
 * Repository interface for Coupon entity operations.
 * 
 * <p>This repository provides database access methods for coupon management,
 * including validation, usage tracking, and availability checking.</p>
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Repository
public interface CouponRepository extends JpaRepository<Coupon, UUID> {

    /**
     * Finds a coupon by its unique code.
     *
     * @param code The coupon code to search for
     * @return The coupon if found, null otherwise
     */
    Coupon findByCode(String code);

    /**
     * Finds active coupons that are currently valid.
     * 
     * <p>Returns coupons that are:
     * <ul>
     *   <li>Active status</li>
     *   <li>Within valid date range (start date to expiry date)</li>
     * </ul>
     * </p>
     *
     * @param currentDate The current date/time for validation
     * @return List of valid and active coupons
     */
    @Query("SELECT c FROM Coupon c WHERE c.status = 'ACTIVE' " +
           "AND (c.startDate IS NULL OR c.startDate <= :currentDate) " +
           "AND (c.expiryDate IS NULL OR c.expiryDate >= :currentDate)")
    List<Coupon> findActiveCoupons(@Param("currentDate") LocalDateTime currentDate);

    /**
     * Finds coupons by scope that are currently valid.
     *
     * @param couponScope The scope to filter by (ORDER or SHIPPING)
     * @param currentDate The current date/time for validation
     * @return List of valid coupons for the specified scope
     */
    @Query("SELECT c FROM Coupon c WHERE c.status = 'ACTIVE' " +
           "AND c.couponScope = :couponScope " +
           "AND (c.startDate IS NULL OR c.startDate <= :currentDate) " +
           "AND (c.expiryDate IS NULL OR c.expiryDate >= :currentDate)")
    List<Coupon> findActiveCouponsByScope(@Param("couponScope") Coupon.CouponScope couponScope, 
                                         @Param("currentDate") LocalDateTime currentDate);

    /**
     * Checks if a coupon code exists and is active.
     *
     * @param code The coupon code to validate
     * @param currentDate The current date/time for validation
     * @return True if coupon exists and is active, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Coupon c " +
           "WHERE c.code = :code AND c.status = 'ACTIVE' " +
           "AND (c.startDate IS NULL OR c.startDate <= :currentDate) " +
           "AND (c.expiryDate IS NULL OR c.expiryDate >= :currentDate)")
    boolean existsByCodeAndActive(@Param("code") String code, 
                                  @Param("currentDate") LocalDateTime currentDate);

    /**
     * Finds coupons that can be applied to orders with a specific minimum value.
     *
     * @param orderValue The order value to check against minimum requirements
     * @param currentDate The current date/time for validation
     * @return List of coupons that can be applied to the order value
     */
    @Query("SELECT c FROM Coupon c WHERE c.status = 'ACTIVE' " +
           "AND c.minOrderValue <= :orderValue " +
           "AND (c.startDate IS NULL OR c.startDate <= :currentDate) " +
           "AND (c.expiryDate IS NULL OR c.expiryDate >= :currentDate)")
    List<Coupon> findCouponsForOrderValue(@Param("orderValue") BigDecimal orderValue, 
                                         @Param("currentDate") LocalDateTime currentDate);
}
