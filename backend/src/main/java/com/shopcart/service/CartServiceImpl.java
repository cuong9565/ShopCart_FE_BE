package com.shopcart.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopcart.dto.AddToCartRequest;
import com.shopcart.dto.CartItemResponseDTO;
import com.shopcart.dto.UpdateCartRequest;
import com.shopcart.entity.CartItem;
import com.shopcart.entity.Inventory;
import com.shopcart.entity.Product;
import com.shopcart.entity.ProductImage;
import com.shopcart.entity.User;
import com.shopcart.repository.CartItemRepository;
import com.shopcart.repository.InventoryRepository;
import com.shopcart.repository.ProductImageRepository;
import com.shopcart.repository.ProductRepository;
import com.shopcart.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for shopping cart operations.
 *
 * <p>This service provides business logic for managing user shopping carts,
 * including validation, inventory checking, and cart persistence.</p>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-09
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {
    
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final ProductImageRepository productImageRepository;
    
    @Override
    @Transactional(readOnly = true)
    public List<CartItemResponseDTO> getCartItems(UUID userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        return cartItems.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public CartItemResponseDTO addToCart(UUID userId, AddToCartRequest request) {
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        
        // Validate product exists and is active
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + request.getProductId()));
        
        if (!"ACTIVE".equals(product.getStatus())) {
            throw new IllegalArgumentException("Product is not available: " + product.getStatus());
        }
        
        // Check inventory
        Inventory inventory = inventoryRepository.findByProductId(product.getId());
        if (inventory == null || inventory.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient inventory for product: " + product.getName());
        }
        
        // Check if product already exists in cart
        CartItem existingCartItem = cartItemRepository.findByUserIdAndProductId(userId, request.getProductId());
        
        if (existingCartItem != null) {
            // Update existing cart item quantity
            int newQuantity = existingCartItem.getQuantity() + request.getQuantity();
            
            // Check inventory again for new total quantity
            if (inventory.getQuantity() < newQuantity) {
                throw new IllegalArgumentException("Insufficient inventory for product: " + product.getName());
            }
            
            existingCartItem.setQuantity(newQuantity);
            CartItem updatedCartItem = cartItemRepository.save(existingCartItem);
            return convertToResponseDTO(updatedCartItem);
        } else {
            // Create new cart item
            CartItem newCartItem = new CartItem(user, product, request.getQuantity());
            CartItem savedCartItem = cartItemRepository.save(newCartItem);
            return convertToResponseDTO(savedCartItem);
        }
    }
    
    @Override
    public CartItemResponseDTO updateCartItemQuantity(UUID userId, UUID cartItemId, UpdateCartRequest request) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Cart item not found with id: " + cartItemId));
        
        // Validate cart item belongs to user
        if (!cartItem.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("Cart item does not belong to user");
        }
        
        // Check inventory
        Inventory inventory = inventoryRepository.findByProductId(cartItem.getProduct().getId());
        if (inventory == null || inventory.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("Insufficient inventory for product: " + cartItem.getProduct().getName());
        }
        
        cartItem.setQuantity(request.getQuantity());
        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        return convertToResponseDTO(updatedCartItem);
    }

    @Override
    public CartItemResponseDTO updateProductQuantityFromCart(UUID userId, UUID productId, Integer quantity) {
        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(userId, productId);
        if (cartItem == null) {
            throw new IllegalArgumentException("Cart item not found for user and product");
        }
        
        // Check inventory
        Inventory inventory = inventoryRepository.findByProductId(cartItem.getProduct().getId());
        if (inventory == null || inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient inventory for product: " + cartItem.getProduct().getName());
        }
        
        cartItem.setQuantity(quantity);
        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        return convertToResponseDTO(updatedCartItem);
    }
    
    @Override
    public boolean removeFromCart(UUID userId, UUID cartItemId) {
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElse(null);
        
        if (cartItem == null || !cartItem.getUser().getId().equals(userId)) {
            return false;
        }
        
        cartItemRepository.delete(cartItem);
        return true;
    }
    
    @Override
    public boolean removeProductFromCart(UUID userId, UUID productId) {
        int deletedRows = cartItemRepository.deleteByUserIdAndProductId(userId, productId);
        return deletedRows > 0;
    }
    
    @Override
    public int clearCart(UUID userId) {
        return cartItemRepository.deleteByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getCartItemCount(UUID userId) {
        return cartItemRepository.countByUserId(userId);
    }
    
    @Override
    @Transactional(readOnly = true)
    public long getTotalCartQuantity(UUID userId) {
        return cartItemRepository.getTotalQuantityByUserId(userId);
    }
    
    /**
     * Converts a CartItem entity to CartItemResponseDTO.
     *
     * @param cartItem The cart item entity to convert
     * @return The corresponding DTO
     */
    private CartItemResponseDTO convertToResponseDTO(CartItem cartItem) {
        CartItemResponseDTO dto = new CartItemResponseDTO();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setProductPrice(cartItem.getProduct().getPrice());
        
        // Get thumbnail image for the product
        ProductImage thumbnailImage = productImageRepository.findThumbnailByProductId(cartItem.getProduct().getId());
        if (thumbnailImage != null) {
            dto.setThumbnailImage(thumbnailImage.getImageUrl());
        }
        
        dto.setQuantity(cartItem.getQuantity());
        dto.setSubtotal(dto.calculateSubtotal());
        dto.setProductStatus(cartItem.getProduct().getStatus());
        dto.setProductSlug(cartItem.getProduct().getSlug());
        dto.setCreatedAt(cartItem.getCreatedAt());
        
        return dto;
    }
}
