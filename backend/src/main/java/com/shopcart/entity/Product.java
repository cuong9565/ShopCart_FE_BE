package com.shopcart.entity;

import java.math.BigDecimal;
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
import lombok.Data;

/**
 * Entity representing a product in the ShopCart e-commerce system.
 *
 * <p>This entity manages core product information for:
 * <ul>
 *   <li>Product catalog and inventory management</li>
 *   <li>Pricing and sales information</li>
 *   <li>Product categorization and organization</li>
 *   <li>Featured products management for marketing</li>
 *   <li>SEO-friendly URL generation</li>
 * </ul>
 *
 * <p><b>Business Purpose:</b> Central entity for product management,
 * supporting e-commerce operations including sales, inventory, and marketing.</p>
 *
 * <p><b>Product Lifecycle:</b>
 * <ul>
 *   <li>Products start as ACTIVE by default</li>
 *   <li>Can be marked as featured for promotional display</li>
 *   <li>Status can be changed for inventory management</li>
 *   <li>Slug provides SEO-friendly URLs for marketing</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Data
@Entity
@Table(name = "product")
public class Product {
    
    /**
     * Primary key identifier for the product.
     *
     * <p>Uses UUID generation strategy to ensure globally unique identifiers
     * across all products and prevent ID collisions in distributed systems.</p>
     *
     * <p><b>Business Logic:</b> UUID provides better security than sequential IDs
     * as it prevents enumeration attacks on product catalog.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Name of the product for display and identification.
     *
     * <p>This field serves as the primary human-readable identifier for the product
     * and is used throughout the application for:
     * <ul>
     *   <li>Product listings and search results</li>
     *   <li>Shopping cart and order processing</li>
     *   <li>Product detail pages and descriptions</li>
     *   <li>Inventory management and reporting</li>
     * </ul>
     *
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>Required field for product creation</li>
     *   <li>Maximum length of 255 characters for display consistency</li>
     *   <li>Should be descriptive and user-friendly</li>
     * </ul>
     */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /**
     * Price of the product in the system's currency.
     *
     * <p>Stores the selling price with high precision for accurate financial calculations.
     * Used for:
     * <ul>
     *   <li>Shopping cart calculations and order totals</li>
     *   <li>Product pricing and display</li>
     *   <li>Financial reporting and analytics</li>
     *   <li>Promotional pricing and discounts</li>
     * </ul>
     *
     * <p><b>Technical Details:</b>
     * <ul>
     *   <li>Uses BigDecimal for precise financial calculations</li>
     *   <li>Precision of 12 digits with 2 decimal places</li>
     *   <li>Supports values up to 99,999,999.99</li>
     * </ul>
     *
     * <p><b>Business Rule:</b> Required field - all products must have a price.</p>
     */
    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    /**
     * Detailed description of the product for marketing and information purposes.
     *
     * <p>Provides comprehensive product information for:
     * <ul>
     *   <li>Product detail pages and marketing materials</li>
     *   <li>Customer purchasing decisions</li>
     *   <li>Search engine optimization (SEO)</li>
     *   <li>Product specifications and features</li>
     * </ul>
     *
     * <p><b>Technical Details:</b>
     * <ul>
     *   <li>Uses TEXT column type for unlimited length</li>
     *   <li>Supports HTML formatting for rich content</li>
     *   <li>Optional field - products can exist without description</li>
     * </ul>
     *
     * <p><b>Business Logic:</b> Essential for product marketing and customer information.</p>
     */
    @Column(name = "description", columnDefinition = "text")
    private String description;

    /**
     * Status of the product in the e-commerce system.
     *
     * <p>Controls product visibility and availability for:
     * <ul>
     *   <li>Product catalog display and search</li>
     *   <li>Order processing and inventory management</li>
     *   <li>Administrative product management</li>
     *   <li>System reporting and analytics</li>
     * </ul>
     *
     * <p><b>Status Values:</b>
     * <ul>
     *   <li>ACTIVE - Product is available for purchase (default)</li>
     *   <li>INACTIVE - Product exists but not available</li>
     *   <li>DISCONTINUED - Product no longer sold</li>
     *   <li>OUT_OF_STOCK - Temporarily unavailable</li>
     * </ul>
     *
     * <p><b>Business Rule:</b> Default value is ACTIVE for new products.</p>
     */
    @Column(name = "status", length = 255)
    private String status = "ACTIVE";

    /**
     * Flag indicating whether the product should be featured in promotional displays.
     *
     * <p>Controls product visibility in special marketing sections for:
     * <ul>
     *   <li>Homepage featured products carousel</li>
     *   <li>Promotional banners and marketing campaigns</li>
     *   <li>Special product collections and highlights</li>
     *   <li>Marketing analytics and reporting</li>
     * </ul>
     *
     * <p><b>Business Logic:</b>
     * <ul>
     *   <li>Featured products receive priority display placement</li>
     *   <li>Used for marketing and promotional strategies</li>
     *   <li>Default value is false for new products</li>
     *   <li>Can be toggled by administrators for marketing campaigns</li>
     * </ul>
     */
    @Column(name = "is_featured", nullable = false)
    private Boolean isFeatured = false;

    /**
     * SEO-friendly URL slug for the product.
     *
     * <p>Provides human-readable URLs for:
     * <ul>
     *   <li>Search engine optimization (SEO)</li>
     *   <li>User-friendly product page URLs</li>
     *   <li>Marketing and sharing purposes</li>
     *   <li>Product identification without numeric IDs</li>
     * </ul>
     *
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>Must be unique across all products</li>
     *   <li>Typically derived from product name</li>
     *   <li>Maximum length of 255 characters</li>
     *   <li>Contains only URL-safe characters</li>
     * </ul>
     *
     * <p><b>Example:</b> "premium-laptop-pro-2023" instead of "/products/12345"</p>
     */
    @Column(name = "slug", unique = true, length = 255)
    private String slug;

    /**
     * Timestamp indicating when the product was created.
     *
     * <p>Automatically set to current time when product object is instantiated.
     * Used for:
     * <ul>
     *   <li>Product lifecycle management</li>
     *   <li>Inventory and sales analytics</li>
     *   <li>Reporting on product performance</li>
     *   <li>New product identification and sorting</li>
     * </ul>
     *
     * <p><b>Business Logic:</b> Automatically managed - never manually set after creation.
     * Essential for product management and analytics.</p>
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Category association for product organization.
     *
     * <p>Establishes many-to-one relationship with Category entity for:
     * <ul>
     *   <li>Product categorization and navigation</li>
     *   <li>Inventory management by category</li>
     *   <li>Search and filtering functionality</li>
     *   <li>Analytics and reporting by category</li>
     * </ul>
     *
     * <p><b>Business Logic:</b>
     * <ul>
     *   <li>Optional field - products can exist without category</li>
     *   <li>Foreign key relationship to category table</li>
     *   <li>Supports category-based product organization</li>
     * </ul>
     */
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * Default constructor for Product entity.
     *
     * <p>Automatically initializes default values to ensure
     * all products have valid initial state for system operation.</p>
     *
     * <p><b>Default Values:</b>
     * <ul>
     *   <li>Creation timestamp set to current time</li>
     *   <li>Status set to ACTIVE for immediate availability</li>
     *   <li>Featured flag set to false (not featured by default)</li>
     * </ul>
     *
     * <p><b>Business Rule:</b> New products should be immediately available
     * unless explicitly configured otherwise.</p>
     */
    public Product() {
        this.createdAt = LocalDateTime.now();
        this.status = "ACTIVE";
        this.isFeatured = false;
    }

    /**
     * Parameterized constructor for creating new products.
     *
     * <p>Convenience constructor that initializes essential product information
     * while automatically setting default values for system fields.</p>
     *
     * <p><b>Usage:</b> Typically used during product creation process
     * in admin panels or bulk product import operations.</p>
     *
     * @param name Product name for display and identification (required)
     * @param price Product selling price (required, positive value)
     * @param description Detailed product description (optional)
     * @param category Product category for organization (optional)
     * @param slug SEO-friendly URL slug (required, unique)
     */
    public Product(String name, BigDecimal price, String description, Category category, String slug) {
        this();
        this.name = name;
        this.price = price;
        this.description = description;
        this.category = category;
        this.slug = slug;
    }
}
