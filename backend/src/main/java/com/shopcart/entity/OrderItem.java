package com.shopcart.entity;

import java.math.BigDecimal;
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
 * Entity representing the order_items table in database
 * Stores individual products within an order with their prices at purchase time
 */
@Entity
@Data
@Table(name = "order_items")
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    /**
     * Primary key for the order item
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     * Order that contains this item
     */
    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    /**
     * Product that was purchased
     */
    @Column(name = "product_id", nullable = false)
    private UUID productId;

    /**
     * Quantity of the product purchased
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    /**
     * Price of the product at the time of purchase (snapshot)
     */
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;
}
