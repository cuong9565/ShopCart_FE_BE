package com.shopcart.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shopcart.entity.CartItem;

/**
 * Repository interface for CartItem entity operations.
 *
 * <p>This repository provides data access methods for shopping cart management,
 * supporting CRUD operations and custom queries for cart functionality.</p>
 *
 * <p><b>Key Operations:</b>
 * <ul>
 *   <li>Find cart items by user</li>
 *   <li>Check if product exists in user's cart</li>
 *   <li>Update cart item quantities</li>
 *   <li>Remove items from cart</li>
 *   <li>Get cart items ordered by creation time</li>
 * </ul>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {

    /**
     * Finds all cart items for a specific user, ordered by creation time (oldest first).
     *
     * <p>This method is used to retrieve the complete shopping cart for a user,
     * with items ordered by the time they were added to the cart.</p>
     *
     * @param userId The UUID of the user whose cart items to retrieve
     * @return List of cart items ordered by creation time (oldest first)
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.user.id = :userId ORDER BY ci.createdAt ASC")
    List<CartItem> findByUserIdOrderByCreatedAtAsc(@Param("userId") UUID userId);

    /**
     * Finds a specific cart item for a user and product combination.
     *
     * <p>This method is used to check if a product is already in a user's cart
     * and to retrieve the specific cart item for updates.</p>
     *
     * @param userId The UUID of the user
     * @param productId The UUID of the product
     * @return The cart item if found, null otherwise
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = :productId")
    CartItem findByUserIdAndProductId(@Param("userId") UUID userId, @Param("productId") UUID productId);

    /**
     * Deletes a specific cart item for a user.
     *
     * <p>This method is used to remove a single product from the user's cart.</p>
     *
     * @param userId The UUID of the user
     * @param productId The UUID of the product to remove from cart
     * @return Number of rows affected (1 if successful, 0 if not found)
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = :productId")
    int deleteByUserIdAndProductId(@Param("userId") UUID userId, @Param("productId") UUID productId);
}
