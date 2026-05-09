package com.shopcart.service;

import java.util.List;
import java.util.UUID;

import com.shopcart.dto.AddToCartRequest;
import com.shopcart.dto.CartItemResponseDTO;
import com.shopcart.dto.UpdateCartRequest;

/**
 * Service interface for shopping cart operations.
 *
 * <p>This service provides business logic for managing user shopping carts,
 * including adding items, updating quantities, removing items, and retrieving cart contents.</p>
 *
 * <p><b>Key Operations:</b>
 * <ul>
 *   <li>Get all cart items for a user (ordered by creation time)</li>
 *   <li>Add products to cart with quantity validation</li>
 *   <li>Update cart item quantities</li>
 *   <li>Remove items from cart</li>
 *   <li>Clear entire cart</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
public interface CartService {

    /**
     * Retrieves all cart items for a specific user, ordered by creation time (oldest first).
     *
     * <p>This method returns the complete shopping cart for the authenticated user,
     * with items sorted by the time they were added to the cart.</p>
     *
     * @param userId The UUID of the user whose cart to retrieve
     * @return List of cart items ordered by creation time (oldest first)
     */
    List<CartItemResponseDTO> getCartItems(UUID userId);

    /**
     * Adds a product to the user's shopping cart.
     *
     * <p>If the product already exists in the cart, the quantity will be incremented.
     * If the product is not in the cart, a new cart item will be created.</p>
     *
     * @param userId The UUID of the user adding the product to cart
     * @param request The add to cart request containing product ID and quantity
     * @return The updated or created cart item
     * @throws IllegalArgumentException if product is not found, inactive, or insufficient inventory
     */
    CartItemResponseDTO addToCart(UUID userId, AddToCartRequest request);

    /**
     * Updates the quantity of a specific cart item.
     *
     * <p>This method modifies the quantity of an existing cart item.
     * The quantity must be a positive integer.</p>
     *
     * @param userId The UUID of the user who owns the cart
     * @param cartItemId The UUID of the cart item to update
     * @param request The update request containing the new quantity
     * @return The updated cart item
     * @throws IllegalArgumentException if cart item not found, doesn't belong to user, or insufficient inventory
     */
    CartItemResponseDTO updateCartItemQuantity(UUID userId, UUID cartItemId, UpdateCartRequest request);

    /**
     * Updates the quantity of a product in the user's cart.
     *
     * <p>This method modifies the quantity of an existing cart item by product ID.
     * The quantity must be a positive integer.</p>
     *
     * @param userId The UUID of the user who owns the cart
     * @param productId The UUID of the product to update in cart
     * @param quantity The new quantity value
     * @return The updated cart item
     * @throws IllegalArgumentException if cart item not found, doesn't belong to user, or insufficient inventory
     */
    CartItemResponseDTO updateProductQuantityFromCart(UUID userId, UUID productId, Integer quantity);

    /**
     * Removes a specific item from the user's cart.
     *
     * <p>This method deletes a single cart item. If the cart item doesn't exist
     * or doesn't belong to the user, no exception is thrown (idempotent operation).</p>
     *
     * @param userId The UUID of the user who owns the cart
     * @param cartItemId The UUID of the cart item to remove
     * @return true if the item was removed, false if not found
     */
    boolean removeFromCart(UUID userId, UUID cartItemId);

    /**
     * Removes a product from the user's cart by product ID.
     *
     * <p>This method deletes the cart item for a specific product.
     * If the product is not in the cart, no exception is thrown.</p>
     *
     * @param userId The UUID of the user who owns the cart
     * @param productId The UUID of the product to remove from cart
     * @return true if the product was removed, false if not found
     */
    boolean removeProductFromCart(UUID userId, UUID productId);

    /**
     * Clears all items from the user's shopping cart.
     *
     * <p>This method is typically called after successful order completion
     * to empty the user's cart for a fresh start.</p>
     *
     * @param userId The UUID of the user whose cart to clear
     * @return The number of cart items that were removed
     */
    int clearCart(UUID userId);

    /**
     * Gets the total number of distinct products in the user's cart.
     *
     * @param userId The UUID of the user
     * @return Number of distinct products in the cart
     */
    long getCartItemCount(UUID userId);

    /**
     * Gets the total quantity of all items in the user's cart.
     *
     * @param userId The UUID of the user
     * @return Total quantity of all items in the cart
     */
    long getTotalCartQuantity(UUID userId);
}
