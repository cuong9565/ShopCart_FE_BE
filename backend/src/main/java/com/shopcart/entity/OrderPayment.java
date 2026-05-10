package com.shopcart.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing the order_payments table in database
 * Stores payment information for orders
 */
@Entity
@Data
@Table(name = "order_payments")
@NoArgsConstructor
@AllArgsConstructor
public class OrderPayment {

    /**
     * Primary key for the payment
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Order that this payment belongs to
     */
    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    /**
     * Legacy payment method field (integer)
     * Note: Should be replaced by paymentMethodId in future versions
     */
    @Column(name = "method", nullable = false)
    private Integer method;

    /**
     * Payment status
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;

    /**
     * Timestamp when payment was completed
     */
    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    /**
     * Payment method ID reference
     */
    @Column(name = "payment_method_id")
    private UUID paymentMethodId;

    /**
     * Initializes default payment status
     */
    public void initializeDefaults() {
        this.status = PaymentStatus.PENDING;
    }

    /**
     * Enum for payment statuses
     */
    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        CANCELLED
    }
}
