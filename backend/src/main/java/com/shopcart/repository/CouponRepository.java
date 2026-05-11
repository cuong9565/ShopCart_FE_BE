package com.shopcart.repository;

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
}
