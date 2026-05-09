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
import lombok.Data;

/**
 * Entity representing product images in the ShopCart e-commerce system.
 *
 * <p>This entity manages visual content for products including:
 * <ul>
 *   <li>Product gallery images for detail pages</li>
 *   <li>Thumbnail images for listings and previews</li>
 *   <li>Image ordering and display priority</li>
 *   <li>Image URL management and storage references</li>
 * </ul>
 *
 * <p><b>Business Purpose:</b> Provides comprehensive image management
 * to enhance product presentation and customer experience.</p>
 *
 * <p><b>Image Strategy:</b>
 * <ul>
 *   <li>Multiple images per product for comprehensive visualization</li>
 *   <li>Sortable images for controlled display sequence</li>
 *   <li>Thumbnail designation for optimized loading</li>
 *   <li>External URL storage for scalable image hosting</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Entity
@Data
@Table(name = "product_image")
public class ProductImage {
    
    /**
     * Primary key identifier for the product image.
     *
     * <p>Uses UUID generation strategy to ensure globally unique identifiers
     * across all product images and prevent ID collisions in distributed systems.</p>
     *
     * <p><b>Business Logic:</b> UUID provides better security than sequential IDs
     * as it prevents enumeration attacks on image resources.</p>
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * Product association linking this image to a specific product.
     *
     * <p>Establishes many-to-one relationship with Product entity for:
     * <ul>
     *   <li>Image organization by product</li>
     *   <li>Product gallery management</li>
     *   <li>Cascading operations for product deletion</li>
     *   <li>Product-specific image queries</li>
     * </ul>
     *
     * <p><b>Business Logic:</b>
     * <ul>
     *   <li>Required field - all images must belong to a product</li>
     *   <li>Foreign key relationship to product table</li>
     *   <li>Supports multiple images per product</li>
     * </ul>
     */
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    /**
     * URL reference to the stored image file.
     *
     * <p>Contains the complete URL or path to the image file stored externally.
     * Used for:
     * <ul>
     *   <li>Image display in product galleries and detail pages</li>
     *   <li>Thumbnail generation and optimization</li>
     *   <li>Image serving through CDN or external storage</li>
     *   <li>Image processing and manipulation references</li>
     * </ul>
     *
     * <p><b>Technical Details:</b>
     * <ul>
     *   <li>Uses TEXT column type for unlimited URL length</li>
     *   <li>Supports both absolute and relative URLs</li>
     *   <li>Compatible with external storage services (AWS S3, Cloudinary, etc.)</li>
     *   <li>Required field - all images must have a valid URL</li>
     * </ul>
     *
     * <p><b>Business Logic:</b> External URL storage enables scalable image hosting
     * and reduces database storage requirements.</p>
     */
    @Column(name = "image_url", nullable = false, columnDefinition = "text")
    private String imageUrl;

    /**
     * Sort order for image display sequence.
     *
     * <p>Controls the display order of images in product galleries for:
     * <ul>
     *   <li>Product detail page image sequencing</li>
     *   <li>Thumbnail selection priority</li>
     *   <li>Image gallery navigation order</li>
     *   <li>Marketing and presentation control</li>
     * </ul>
     *
     * <p><b>Business Logic:</b>
     * <ul>
     *   <li>Lower numbers appear first in display sequences</li>
     *   <li>Default value is 0 for first-added images</li>
     *   <li>Can be adjusted for optimal visual presentation</li>
     *   <li>Used by repository queries for ordered image retrieval</li>
     * </ul>
     *
     * <p><b>Usage:</b> Typically set during image upload or through admin interface
     * for arranging product visual presentation.</p>
     */
    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder = 0;

    /**
     * Flag indicating whether this image serves as the product thumbnail.
     *
     * <p>Designates the primary representative image for:
     * <ul>
     *   <li>Product listings and category pages</li>
     *   <li>Shopping cart and checkout displays</li>
     *   <li>Search results and preview thumbnails</li>
     *   <li>Social media sharing and marketing materials</li>
     * </ul>
     *
     * <p><b>Business Logic:</b>
     * <ul>
     *   <li>Only one image per product should be marked as thumbnail</li>
     *   <li>Default value is false (not thumbnail)</li>
     *   <li>Used by service layer for thumbnail selection</li>
     *   <li>Can be changed to update product representation</li>
     * </ul>
     *
     * <p><b>Validation:</b> Application logic should ensure only one thumbnail
     * exists per product, though database constraint allows multiple for flexibility.</p>
     */
    @Column(name = "is_thumbnail", nullable = false)
    private Boolean isThumbnail = false;

    /**
     * Timestamp indicating when the image was added to the system.
     *
     * <p>Automatically set to current time when image object is instantiated.
     * Used for:
     * <ul>
     *   <li>Image management and analytics</li>
     *   <li>Content freshness tracking</li>
     *   <li>Backup and archival processes</li>
     *   <li>Image lifecycle management</li>
     * </ul>
     *
     * <p><b>Business Logic:</b> Automatically managed - never manually set after creation.
     * Essential for image tracking and management operations.</p>
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * Default constructor for ProductImage entity.
     *
     * <p>Automatically initializes default values to ensure
     * all images have valid initial state for system operation.</p>
     *
     * <p><b>Default Values:</b>
     * <ul>
     *   <li>Creation timestamp set to current time</li>
     *   <li>Sort order set to 0 (first position)</li>
     *   <li>Thumbnail flag set to false (not thumbnail by default)</li>
     * </ul>
     *
     * <p><b>Business Rule:</b> New images should appear first in galleries
     * and not be thumbnails unless explicitly designated.</p>
     */
    public ProductImage() {
        this.createdAt = LocalDateTime.now();
        this.sortOrder = 0;
        this.isThumbnail = false;
    }

    /**
     * Parameterized constructor for creating new product images.
     *
     * <p>Convenience constructor that initializes image information
     * while automatically setting default values for system fields.</p>
     *
     * <p><b>Usage:</b> Typically used during:
     * <ul>
     *   <li>Image upload processes for new products</li>
     *   <li>Bulk image import operations</li>
     *   <li>Manual image management by administrators</li>
     * </ul>
     *
     * @param product Associated product for this image
     * @param imageUrl URL reference to the stored image file
     * @param sortOrder Display order for this image in galleries
     * @param isThumbnail Whether this image serves as the product thumbnail
     */
    public ProductImage(Product product, String imageUrl, Integer sortOrder, Boolean isThumbnail) {
        this();
        this.product = product;
        this.imageUrl = imageUrl;
        this.sortOrder = sortOrder;
        this.isThumbnail = isThumbnail;
    }
}
