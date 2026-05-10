package com.shopcart.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopcart.dto.AddToCartRequest;
import com.shopcart.dto.CartItemResponseDTO;
import com.shopcart.dto.RemoveFromCartRequest;
import com.shopcart.dto.UpdateCartRequest;
import com.shopcart.security.CustomUserDetails;
import com.shopcart.service.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for shopping cart operations.
 *
 * <p>This controller provides endpoints for managing user shopping carts,
 * including retrieving cart contents, adding items, updating quantities,
 * and removing items.</p>
 *
 * <p><b>Endpoints:</b>
 * <ul>
 *   <li>GET /api/cart - Get all cart items for authenticated user</li>
 *   <li>POST /api/cart - Add product to cart</li>
 *   <li>PUT /api/cart - Update cart item quantity (using request body)</li>
 *   <li>DELETE /api/cart - Remove product from cart (using request body)</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
    
    private final CartService cartService;
    
    /**
     * Retrieves all cart items for the authenticated user.
     *
     * <p>Returns the complete shopping cart with items ordered by creation time
     * (oldest items first). Each item includes product details and calculated subtotal.</p>
     *
     * @param userDetails The authenticated user details
     * @return List of cart items ordered by creation time
     */
    @GetMapping
    public ResponseEntity<List<CartItemResponseDTO>> getCartItems(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        List<CartItemResponseDTO> cartItems = cartService.getCartItems(userId);
        return ResponseEntity.ok(cartItems);
    }
    
    /**
     * Adds a product to the authenticated user's cart.
     *
     * <p>If the product already exists in the cart, the quantity will be incremented.
     * If not, a new cart item will be created. Validates product availability
     * and inventory levels.</p>
     *
     * @param request The add to cart request containing product ID and quantity
     * @param userDetails The authenticated user details
     * @return The created or updated cart item
     */
    @PostMapping
    public ResponseEntity<CartItemResponseDTO> addToCart(
            @Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        CartItemResponseDTO cartItem = cartService.addToCart(userId, request);
        return ResponseEntity.ok(cartItem);
    }
    
    /**
     * Updates the quantity of a specific cart item.
     *
     * <p>Modifies the quantity of an existing cart item. Validates that the
     * cart item belongs to the authenticated user and checks inventory availability.</p>
     *
     * @param request The update cart request containing cart item ID and new quantity
     * @param userDetails The authenticated user details
     * @return The updated cart item
     */
    @PutMapping
    public ResponseEntity<CartItemResponseDTO> updateCartItemQuantity(
            @Valid @RequestBody UpdateCartRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        CartItemResponseDTO cartItem = cartService.updateProductQuantityFromCart(userId, request.getProductId(), request.getQuantity());
        return ResponseEntity.ok(cartItem);
    }
    
    /**
     * Removes a cart item from the authenticated user's cart by cart item ID.
     *
     * <p>Deletes the cart item for a specific cart item. If the cart item is not
     * found or doesn't belong to the user, the operation completes successfully.</p>
     *
     * @param request The remove from cart request containing cart item ID
     * @param userDetails The authenticated user details
     * @return Success response with removal status
     */
    @DeleteMapping
    public ResponseEntity<Void> removeProductFromCart(
            @Valid @RequestBody RemoveFromCartRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        boolean removed = cartService.removeProductFromCart(userId, request.getProductId());
        return removed ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
    
    /**
     * Retrieves the total amount of all products in the authenticated user's cart.
     *
     * <p>Returns the calculated sum of (product price × quantity) for all items
     * in the user's shopping cart. Returns 0.00 if the cart is empty.</p>
     *
     * @param userDetails The authenticated user details
     * @return Response containing the total cart amount
     */
    @GetMapping("/total")
    public ResponseEntity<java.math.BigDecimal> getCartTotalAmount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UUID userId = userDetails.getId();
        java.math.BigDecimal totalAmount = cartService.getCartTotalAmount(userId);
        return ResponseEntity.ok(totalAmount);
    }
}
