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
     * Checks if a product exists in a user's cart.
     *
     * <p>This method is used for validation before adding new items to cart
     * to determine whether to create a new item or update existing quantity.</p>
     *
     * @param userId The UUID of the user
     * @param productId The UUID of the product
     * @return true if the product exists in the user's cart, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(ci) > 0 THEN true ELSE false END FROM CartItem ci WHERE ci.user.id = :userId AND ci.product.id = :productId")
    boolean existsByUserIdAndProductId(@Param("userId") UUID userId, @Param("productId") UUID productId);

    /**
     * Updates the quantity of a specific cart item.
     *
     * <p>This method is used to modify the quantity of an existing cart item,
     * typically called when the user changes item quantity in the cart.</p>
     *
     * @param cartItemId The UUID of the cart item to update
     * @param quantity The new quantity value (must be positive)
     * @return Number of rows affected (1 if successful, 0 if not found)
     */
    @Modifying
    @Query("UPDATE CartItem ci SET ci.quantity = :quantity WHERE ci.id = :cartItemId")
    int updateQuantity(@Param("cartItemId") UUID cartItemId, @Param("quantity") Integer quantity);

    /**
     * Deletes all cart items for a specific user.
     *
     * <p>This method is typically used after successful order completion
     * to clear the user's cart.</p>
     *
     * @param userId The UUID of the user whose cart to clear
     * @return Number of cart items deleted
     */
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.user.id = :userId")
    int deleteByUserId(@Param("userId") UUID userId);

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

    /**
     * Counts the total number of items in a user's cart.
     *
     * <p>This method returns the count of distinct products in the cart,
     * not the total quantity of all items.</p>
     *
     * @param userId The UUID of the user
     * @return Number of distinct products in the user's cart
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.user.id = :userId")
    long countByUserId(@Param("userId") UUID userId);

    /**
     * Calculates the total quantity of all items in a user's cart.
     *
     * <p>This method sums up the quantities of all cart items for a user,
     * useful for cart display and inventory checking.</p>
     *
     * @param userId The UUID of the user
     * @return Total quantity of all items in the cart
     */
    @Query("SELECT COALESCE(SUM(ci.quantity), 0) FROM CartItem ci WHERE ci.user.id = :userId")
    long getTotalQuantityByUserId(@Param("userId") UUID userId);
}
