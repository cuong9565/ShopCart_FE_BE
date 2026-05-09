package com.shopcart.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for remove from cart requests.
 *
 * <p>This DTO validates and encapsulates the data required to remove
 * a product from a user's shopping cart.</p>
 *
 * <p><b>Validation Rules:</b>
 * <ul>
 *   <li>Product ID is required and must be a valid UUID</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Data
public class RemoveFromCartRequest {
    
    /**
     * Product ID to identify which cart item to remove.
     *
     * <p>Required field - must be a valid UUID.</p>
     */
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    /**
     * Default constructor.
     */
    public RemoveFromCartRequest() {
    }
    
    /**
     * Parameterized constructor for creating remove from cart requests.
     *
     * @param productId The UUID of the product to remove
     */
    public RemoveFromCartRequest(UUID productId) {
        this.productId = productId;
    }
}
