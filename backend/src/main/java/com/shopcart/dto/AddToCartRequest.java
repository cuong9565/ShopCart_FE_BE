package com.shopcart.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for add to cart requests.
 *
 * <p>This DTO validates and encapsulates the data required to add a product
 * to a user's shopping cart.</p>
 *
 * <p><b>Validation Rules:</b>
 * <ul>
 *   <li>Product ID is required and must be a valid UUID</li>
 *   <li>Quantity is required and must be at least 1</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Data
public class AddToCartRequest {
    
    /**
     * Unique identifier of the product to add to cart.
     *
     * <p>Required field - must reference an existing product in the system.</p>
     */
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    /**
     * Quantity of the product to add to cart.
     *
     * <p>Required field - must be a positive integer (minimum 1).</p>
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    /**
     * Default constructor.
     */
    public AddToCartRequest() {
    }
    
    /**
     * Parameterized constructor for creating add to cart requests.
     *
     * @param productId The UUID of the product to add
     * @param quantity The quantity to add (must be positive)
     */
    public AddToCartRequest(UUID productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}
