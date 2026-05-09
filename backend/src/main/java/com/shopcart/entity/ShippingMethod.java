package com.shopcart.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the shipping_methods table in database
 * Stores available shipping methods for orders
 */
@Entity
@Data
@Table(name = "shipping_methods")
@NoArgsConstructor
@AllArgsConstructor
public class ShippingMethod {

    /**
     * Primary key for the shipping method
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Unique code for the shipping method
     */
    @Column(name = "code", nullable = false, unique = true, length = 255)
    private String code;

    /**
     * Display name of the shipping method
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Description of the shipping method
     */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /**
     * Base fee for the shipping method
     */
    @Column(name = "base_fee", nullable = false, precision = 19, scale = 3)
    private BigDecimal baseFee;

    /**
     * Minimum estimated delivery days
     */
    @Column(name = "estimated_days_min")
    private Integer estimatedDaysMin;

    /**
     * Maximum estimated delivery days
     */
    @Column(name = "estimated_days_max")
    private Integer estimatedDaysMax;

    /**
     * Flag indicating if the shipping method is active
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    /**
     * Timestamp when the shipping method was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
