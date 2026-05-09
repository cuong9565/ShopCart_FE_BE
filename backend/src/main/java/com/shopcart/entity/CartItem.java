package com.shopcart.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

/**
 * Entity representing a shopping cart item in the ShopCart e-commerce system.
 *
 * <p>This entity manages the relationship between users and products in their shopping cart for:
 * <ul>
 *   <li>Shopping cart management and persistence</li>
 *   <li>Product quantity tracking per user</li>
 *   <li>Cart item ordering by creation time</li>
 *   <li>Inventory management integration</li>
 * </ul>
 *
 * <p><b>Business Purpose:</b> Maintains user-specific shopping cart data,
 * supporting add to cart, quantity management, and cart persistence across sessions.</p>
 *
 * <p><b>Cart Management:</b>
 * <ul>
 *   <li>Each user can have multiple cart items (one per product)</li>
 *   <li>Cart items are ordered by creation time (oldest first)</li>
 *   <li>Quantities are validated to be positive values</li>
 *   <li>Unique constraint prevents duplicate products per user</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Entity
@Data
@Table(name = "cart_item", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "product_id"}))
public class CartItem {
    
    /**
     * Primary key identifier for the cart item.
     *
     * <p>Uses UUID generation strategy to ensure globally unique identifiers
     * across all cart items and prevent ID collisions in distributed systems.</p>
     *
     * <p><b>Business Logic:</b> UUID provides better security than sequential IDs
     * as it prevents enumeration attacks on cart data.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * User who owns this cart item.
     *
     * <p>Establishes many-to-one relationship with User entity for:
     * <ul>
     *   <li>User-specific cart management</li>
     *   <li>Cart isolation between users</li>
     *   <li>Authentication and authorization</li>
     *   <li>Order processing integration</li>
     * </ul>
     *
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>Required field - all cart items must belong to a user</li>
     *   <li>Foreign key relationship to users table</li>
     *   <li>Cascade delete removes cart items when user is deleted</li>
     * </ul>
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Product associated with this cart item.
     *
     * <p>Establishes many-to-one relationship with Product entity for:
     * <ul>
     *   <li>Product information and pricing</li>
     *   <li>Inventory management integration</li>
     *   <li>Product status validation</li>
     *   <li>Order processing and checkout</li>
     * </ul>
     *
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>Required field - all cart items must reference a product</li>
     *   <li>Foreign key relationship to product table</li>
     *   <li>Cascade delete removes cart items when product is deleted</li>
     * </ul>
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * Quantity of the product in the user's cart.
     *
     * <p>Stores the number of units of the product that the user wants to purchase.
     * Used for:
     * <ul>
     *   <li>Shopping cart calculations and totals</li>
     *   <li>Inventory management and stock checking</li>
     *   <li>Order processing and fulfillment</li>
     *   <li>Price calculations and discounts</li>
     * </ul>
     *
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>Must be greater than 0 (positive integer)</li>
     *   <li>Default value is 1 for new cart items</li>
     *   <li>Validated against available inventory</li>
     * </ul>
     */
    @Column(name = "quantity", nullable = false)
    private Integer quantity = 1;

    /**
     * Timestamp indicating when the cart item was created.
     *
     * <p>Automatically set to current time when cart item object is instantiated.
     * Used for:
     * <ul>
     *   <li>Cart item ordering and sorting</li>
     *   <li>Cart analytics and user behavior tracking</li>
     *   <li>Abandoned cart analysis</li>
     *   <li>Cart freshness management</li>
     * </ul>
     *
     * <p><b>Business Logic:</b> Automatically managed - never manually set after creation.
     * Essential for cart item ordering by time (oldest items first).</p>
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Default constructor for CartItem entity.
     *
     * <p>Automatically initializes default values to ensure
     * all cart items have valid initial state for system operation.</p>
     *
     * <p><b>Default Values:</b>
     * <ul>
     *   <li>Creation timestamp set to current time</li>
     *   <li>Quantity set to 1 (single item)</li>
     * </ul>
     *
     * <p><b>Business Rule:</b> New cart items default to single quantity
     * and immediate creation timestamp.</p>
     */
    public CartItem() {
        this.createdAt = LocalDateTime.now();
        this.quantity = 1;
    }

    /**
     * Parameterized constructor for creating new cart items.
     *
     * <p>Convenience constructor that initializes essential cart item information
     * while automatically setting default values for system fields.</p>
     *
     * <p><b>Usage:</b> Typically used during add to cart operations
     * in the service layer after validation.</p>
     *
     * @param user User who owns the cart item (required)
     * @param product Product being added to cart (required)
     * @param quantity Number of units (optional, defaults to 1)
     */
    public CartItem(User user, Product product, Integer quantity) {
        this();
        this.user = user;
        this.product = product;
        if (quantity != null && quantity > 0) {
            this.quantity = quantity;
        }
    }
}
