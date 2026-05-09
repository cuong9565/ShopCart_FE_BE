package com.shopcart.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * Entity representing a product category in the ShopCart e-commerce system.
 *
 * <p>This entity manages product categorization for:
 * <ul>
 *   <li>Product organization and navigation</li>
 *   <li>Inventory management by category</li>
 *   <li>Search and filtering functionality</li>
 *   <li>Analytics and reporting by product category</li>
 * </ul>
 *
 * <p><b>Business Purpose:</b> Provides hierarchical organization of products
 * to improve user experience and enable efficient product management.</p>
 *
 * <p><b>Categorization Strategy:</b>
 * <ul>
 *   <li>Flat category structure for simplicity</li>
 *   <li>Unique category names to prevent confusion</li>
 *   <li>Timestamp tracking for category management analytics</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Entity
@Data
@Table(name = "category")
public class Category {
    
    /**
     * Primary key identifier for the category.
     *
     * <p>Uses UUID generation strategy to ensure globally unique identifiers
     * across all categories and prevent ID collisions in distributed systems.</p>
     *
     * <p><b>Business Logic:</b> UUID provides better security and scalability
     * than sequential IDs for category identification.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Name of the product category.
     *
     * <p>This field serves as the primary human-readable identifier for the category
     * and is used throughout the application for:
     * <ul>
     *   <li>Category navigation menus and filters</li>
     *   <li>Product categorization and organization</li>
     *   <li>Search and browsing functionality</li>
     *   <li>Analytics and reporting by category</li>
     * </ul>
     *
     * <p><b>Business Rules:</b>
     * <ul>
     *   <li>Must be unique across all categories to prevent duplication</li>
     *   <li>Required field for category creation</li>
     *   <li>Maximum length of 255 characters for display consistency</li>
     *   <li>Case-sensitive uniqueness for user experience</li>
     * </ul>
     *
     * <p><b>Validation:</b> Category names should be descriptive and user-friendly
     * for optimal navigation experience.</p>
     */
    @Column(name = "name", unique = true, nullable = false, length = 255)
    private String name;

    /**
     * Timestamp indicating when the category was created.
     *
     * <p>Automatically set to current time when category object is instantiated.
     * Used for:
     * <ul>
     *   <li>Category management analytics</li>
     *   <li>Auditing category creation and modifications</li>
     *   <li>Reporting on category lifecycle</li>
     *   <li>System performance monitoring</li>
     * </ul>
     *
     * <p><b>Business Logic:</b> Automatically managed - never manually set after creation.
     * Essential for tracking category management activities.</p>
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Default constructor for Category entity.
     *
     * <p>Automatically initializes the creation timestamp to ensure
     * all categories have a valid creation time for auditing purposes.</p>
     *
     * <p><b>Business Rule:</b> Creation timestamp is mandatory for category
     * lifecycle tracking and analytics.</p>
     */
    public Category() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Parameterized constructor for creating new categories.
     *
     * <p>Convenience constructor that initializes category name
     * while automatically setting the creation timestamp.</p>
     *
     * <p><b>Usage:</b> Typically used during category creation process
     * in admin panels or bulk category import operations.</p>
     *
     * @param name Category name for display and navigation (required, unique)
     */
    public Category(String name) {
        this();
        this.name = name;
    }
}
