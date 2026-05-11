package com.shopcart.service;

import java.util.List;
import java.util.UUID;

import com.shopcart.dto.AddToCartRequest;
import com.shopcart.dto.CartItemResponseDTO;
import com.shopcart.dto.CartPricingRequest;
import com.shopcart.dto.CartPricingResponse;

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
     * Calculates the total amount of all products in the user's cart.
     *
     * <p>This method calculates the sum of (product price × quantity) for all items
     * in the user's shopping cart. Returns 0.00 if the cart is empty.</p>
     *
     * @param userId The UUID of the user
     * @return Total amount of all products in the cart
     */
    java.math.BigDecimal getCartTotalAmount(UUID userId);

    /**
     * Calculates comprehensive pricing for the user's cart including discounts and shipping.
     *
     * <p>This method provides detailed pricing calculation including:
     * <ul>
     *   <li>Total product amount before discounts</li>
     *   <li>Applied coupon discounts (order and shipping)</li>
     *   <li>Shipping fees and discounts</li>
     *   <li>Final total amount</li>
     *   <li>Estimated delivery timeframes</li>
     * </ul>
     * </p>
     *
     * @param userId The UUID of the user
     * @param request The pricing request containing coupons and shipping method
     * @return Comprehensive pricing response with all calculated values
     * @throws IllegalArgumentException if user not found, invalid coupons, or shipping method not found
     */
    CartPricingResponse calculateCartPricing(UUID userId, CartPricingRequest request);
}
