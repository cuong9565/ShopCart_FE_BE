package com.shopcart.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

/**
 * Data Transfer Object for cart item responses.
 *
 * <p>This DTO provides a clean representation of cart item data for API responses,
 * including calculated fields like subtotal and product information.</p>
 *
 * <p><b>Included Fields:</b>
 * <ul>
 *   <li>Cart item identification and timestamps</li>
 *   <li>Product information (name, price, description)</li>
 *   <li>Quantity and calculated subtotal</li>
 *   <li>Product status and availability</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Data
public class CartItemResponseDTO {
    
    /**
     * Unique identifier for the cart item.
     */
    private UUID id;
    
    /**
     * Unique identifier for the product.
     */
    private UUID productId;
    
    /**
     * Name of the product.
     */
    private String productName;
    
    /**
     * Unit price of the product.
     */
    private BigDecimal productPrice;
    
    /**
     * Quantity of the product in the cart.
     */
    private Integer quantity;
    
    /**
     * Calculated subtotal (price × quantity).
     */
    private BigDecimal subtotal;
    
    /**
     * Status of the product (ACTIVE, INACTIVE, etc.).
     */
    private String productStatus;
    
    /**
     * SEO-friendly slug for the product.
     */
    private String productSlug;
    
    /**
     * Timestamp when the cart item was created.
     */
    private LocalDateTime createdAt;
    
    /**
     * Default constructor.
     */
    public CartItemResponseDTO() {
    }
    
    /**
     * Calculates the subtotal based on product price and quantity.
     *
     * @return The calculated subtotal (price × quantity)
     */
    public BigDecimal calculateSubtotal() {
        if (productPrice != null && quantity != null) {
            return productPrice.multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}
