package com.shopcart.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entity representing inventory information for products in the ShopCart e-commerce system.
 *
 * <p>This entity manages stock quantities and availability for:
 * <ul>
 *   <li>Real-time inventory tracking and management</li>
 *   <li>Order processing and stock allocation</li>
 *   <li>Product availability display to customers</li>
 *   <li>Inventory reporting and analytics</li>
 * </ul>
 *
 * <p><b>Business Purpose:</b> Maintains accurate stock levels to prevent overselling
 * and enable effective inventory management across the e-commerce platform.</p>
 *
 * <p><b>Inventory Strategy:</b>
 * <ul>
 *   <li>One-to-one relationship with products (each product has one inventory record)</li>
 *   <li>Real-time quantity updates during order processing</li>
 *   <li>Supports zero and negative quantities for backorder management</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Entity
@Data
@Table(name = "inventory")
public class Inventory {
    
    /**
     * Primary key identifier linking inventory to a specific product.
     *
     * <p>Uses the product's UUID as the primary key to establish a one-to-one
     * relationship between products and their inventory records.</p>
     *
     * <p><b>Business Logic:</b>
     * <ul>
     *   <li>Ensures each product has exactly one inventory record</li>
     *   <li>Simplifies inventory lookup by using product ID directly</li>
     *   <li>Prevents duplicate inventory records for the same product</li>
     * </ul>
     *
     * <p><b>Technical Details:</b>
     * <ul>
     *   <li>Foreign key relationship to product table</li>
     *   <li>UUID type matches product entity primary key</li>
     *   <li>No auto-generation - uses existing product ID</li>
     * </ul>
     */
    @Id
    @Column(name = "product_id")
    private UUID productId;

    /**
     * Current stock quantity available for the product.
     *
     * <p>Represents the number of units currently available for purchase.
     * Used for:
     * <ul>
     *   <li>Product availability display on product pages</li>
     *   <li>Order validation and stock allocation</li>
     *   <li>Inventory management and restocking decisions</li>
     *   <li>Low stock alerts and notifications</li>
     * </ul>
     *
     * <p><b>Business Logic:</b>
     * <ul>
     *   <li>Positive values indicate available stock</li>
     *   <li>Zero value means product is out of stock</li>
     *   <li>Negative values can indicate backorders or overselling</li>
     *   <li>Updated in real-time during order processing</li>
     * </ul>
     *
     * <p><b>Validation Rules:</b>
     * <ul>
     *   <li>Required field - all products must have inventory quantity</li>
     *   <li>Integer type for whole units only</li>
     *   <li>No maximum limit for high-volume products</li>
     * </ul>
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    /**
     * Default constructor for Inventory entity.
     *
     * <p>Required by JPA for entity instantiation and reflection operations.
     * Does not initialize any fields - they will be set through setters or
     * parameterized constructor.</p>
     *
     * <p><b>Usage:</b> Primarily used by JPA provider for entity management
     * and database operations. Not typically used directly in application code.</p>
     */
    public Inventory() {
        // Constructor for JPA
    }

    /**
     * Parameterized constructor for creating new inventory records.
     *
     * <p>Convenience constructor that initializes inventory information
     * for a specific product with initial stock quantity.</p>
     *
     * <p><b>Usage:</b> Typically used during:
     * <ul>
     *   <li>Product creation process to establish initial inventory</li>
     *   <li>Inventory import operations from external systems</li>
     *   <li>Manual inventory adjustments by administrators</li>
     * </ul>
     *
     * @param productId Unique identifier of the associated product
     * @param quantity Initial stock quantity for the product
     */
    public Inventory(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
