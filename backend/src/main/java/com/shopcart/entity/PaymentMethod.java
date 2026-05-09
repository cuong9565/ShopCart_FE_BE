package com.shopcart.entity;

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
 * Entity representing the payment_methods table in database
 * Stores available payment methods for orders
 */
@Entity
@Data
@Table(name = "payment_methods")
@NoArgsConstructor
@AllArgsConstructor
public class PaymentMethod {

    /**
     * Primary key for the payment method
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Unique code for the payment method
     */
    @Column(name = "code", nullable = false, unique = true, length = 50)
    private String code;

    /**
     * Display name of the payment method
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Flag indicating if the payment method is active
     */
    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive;

    /**
     * Timestamp when the payment method was created
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
