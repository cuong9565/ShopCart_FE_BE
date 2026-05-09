package com.shopcart.dto;

import java.util.UUID;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Data Transfer Object for update cart quantity requests.
 *
 * <p>This DTO validates and encapsulates the data required to update
 * the quantity of an existing cart item.</p>
 *
 * <p><b>Validation Rules:</b>
 * <ul>
 *   <li>Quantity is required and must be at least 1</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Data
public class UpdateCartRequest {
    
    /**
     * Product ID to identify which cart item to update.
     *
     * <p>Required field - must be a valid UUID.</p>
     */
    @NotNull(message = "Product ID is required")
    private UUID productId;
    
    /**
     * New quantity for the cart item.
     *
     * <p>Required field - must be a positive integer (minimum 1).</p>
     */
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    /**
     * Default constructor.
     */
    public UpdateCartRequest() {
    }
    
    /**
     * Parameterized constructor for creating update cart requests.
     *
     * @param quantity The new quantity (must be positive)
     */
    public UpdateCartRequest(Integer quantity) {
        this.quantity = quantity;
    }
}
