package com.shopcart.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopcart.dto.AddToCartRequest;
import com.shopcart.dto.CartItemResponseDTO;
import com.shopcart.dto.CartPricingRequest;
import com.shopcart.dto.CartPricingResponse;
import com.shopcart.entity.CartItem;
import com.shopcart.entity.Coupon;
import com.shopcart.entity.Inventory;
import com.shopcart.entity.Product;
import com.shopcart.entity.ProductImage;
import com.shopcart.entity.ShippingMethod;
import com.shopcart.entity.User;
import com.shopcart.exception.CouponValidationException;
import com.shopcart.repository.CartItemRepository;
import com.shopcart.repository.CouponRepository;
import com.shopcart.repository.InventoryRepository;
import com.shopcart.repository.OrderCouponRepository;
import com.shopcart.repository.ProductImageRepository;
import com.shopcart.repository.ProductRepository;
import com.shopcart.repository.ShippingMethodRepository;
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
    private final CouponRepository couponRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final OrderCouponRepository orderCouponRepository;
    
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
    public boolean removeProductFromCart(UUID userId, UUID productId) {
        int deletedRows = cartItemRepository.deleteByUserIdAndProductId(userId, productId);
        return deletedRows > 0;
    }
    
    @Override
    @Transactional(readOnly = true)
    public java.math.BigDecimal getCartTotalAmount(UUID userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        
        if (cartItems.isEmpty()) {
            return java.math.BigDecimal.ZERO;
        }
        
        return cartItems.stream()
                .map(cartItem -> cartItem.getProduct().getPrice()
                        .multiply(java.math.BigDecimal.valueOf(cartItem.getQuantity())))
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
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

    @Override
    @Transactional(readOnly = true)
    public CartPricingResponse calculateCartPricing(UUID userId, CartPricingRequest request) {
        // Validate user exists
        userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Get cart items and calculate base totals
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        BigDecimal totalProductAmount = calculateCartTotal(cartItems);
        
        // Initialize response
        CartPricingResponse response = new CartPricingResponse();
        response.setTotalProductAmount(totalProductAmount);
        response.setTotalAfterProductDiscounts(totalProductAmount);
        response.setBaseShippingFee(BigDecimal.ZERO);
        response.setFinalShippingFee(BigDecimal.ZERO);
        response.setFinalTotalAmount(totalProductAmount);

        // Apply order coupons
        BigDecimal orderDiscountAmount = BigDecimal.ZERO;
        List<CartPricingResponse.AppliedCouponDTO> appliedCoupons = new ArrayList<>();

        if (request.getCouponCodes() != null && !request.getCouponCodes().isEmpty()) {
            for (String couponCode : request.getCouponCodes()) {
                Coupon coupon = couponRepository.findByCode(couponCode);
                if (coupon == null) {
                    continue; // Skip invalid coupons
                }

                // Validate coupon is active and valid
                try {
                    isCouponValid(coupon, totalProductAmount, userId);
                } catch (CouponValidationException e) {
                    continue; // Skip invalid coupons
                }

                // Only apply order scope coupons to product total
                if (coupon.getCouponScope() == Coupon.CouponScope.ORDER) {
                    BigDecimal discountAmount = calculateOrderDiscount(coupon, response.getTotalAfterProductDiscounts());
                    orderDiscountAmount = orderDiscountAmount.add(discountAmount);

                    // Add to applied coupons list
                    CartPricingResponse.AppliedCouponDTO appliedCoupon = new CartPricingResponse.AppliedCouponDTO();
                    appliedCoupon.setCode(coupon.getCode());
                    appliedCoupon.setDiscountType(coupon.getDiscountType().name());
                    appliedCoupon.setDiscountValue(coupon.getDiscountValue());
                    appliedCoupon.setAppliedAmount(discountAmount);
                    appliedCoupon.setScope(coupon.getCouponScope().name());
                    appliedCoupons.add(appliedCoupon);
                }
            }
        }

        response.setTotalAfterProductDiscounts(response.getTotalProductAmount().subtract(orderDiscountAmount));

        // Handle shipping method and fees
        BigDecimal baseShippingFee = BigDecimal.ZERO;
        BigDecimal shippingDiscountAmount = BigDecimal.ZERO;
        LocalDate estimatedMinDate = null;
        LocalDate estimatedMaxDate = null;
        CartPricingResponse.ShippingMethodDTO shippingMethodDTO = null;

        if (request.getShippingMethodId() != null) {
            ShippingMethod shippingMethod = shippingMethodRepository.findById(request.getShippingMethodId())
                    .orElseThrow(() -> new IllegalArgumentException("Shipping method not found with id: " + request.getShippingMethodId()));

            if (shippingMethod.getIsActive()) {
                baseShippingFee = shippingMethod.getBaseFee();
                Integer minDays = shippingMethod.getEstimatedDaysMin();
                Integer maxDays = shippingMethod.getEstimatedDaysMax();
                
                // Calculate actual delivery dates
                LocalDate currentDate = LocalDate.now();
                if (minDays != null) {
                    estimatedMinDate = currentDate.plusDays(minDays);
                }
                if (maxDays != null) {
                    estimatedMaxDate = currentDate.plusDays(maxDays);
                }

                // Apply shipping coupons
                if (request.getCouponCodes() != null && !request.getCouponCodes().isEmpty()) {
                    for (String couponCode : request.getCouponCodes()) {
                        Coupon coupon = couponRepository.findByCode(couponCode);
                        if (coupon == null) {
                            continue;
                        }

                        try {
                            isCouponValid(coupon, response.getTotalAfterProductDiscounts(), userId);
                        } catch (CouponValidationException e) {
                            continue;
                        }

                        // Only apply shipping scope coupons to shipping fee
                        if (coupon.getCouponScope() == Coupon.CouponScope.SHIPPING) {
                            BigDecimal discountAmount = calculateShippingDiscount(coupon, baseShippingFee);
                            shippingDiscountAmount = shippingDiscountAmount.add(discountAmount);

                            // Add to applied coupons list
                            CartPricingResponse.AppliedCouponDTO appliedCoupon = new CartPricingResponse.AppliedCouponDTO();
                            appliedCoupon.setCode(coupon.getCode());
                            appliedCoupon.setDiscountType(coupon.getDiscountType().name());
                            appliedCoupon.setDiscountValue(coupon.getDiscountValue());
                            appliedCoupon.setAppliedAmount(discountAmount);
                            appliedCoupon.setScope(coupon.getCouponScope().name());
                            appliedCoupons.add(appliedCoupon);
                        }
                    }
                }

                // Create shipping method DTO
                shippingMethodDTO = new CartPricingResponse.ShippingMethodDTO();
                shippingMethodDTO.setId(shippingMethod.getId());
                shippingMethodDTO.setCode(shippingMethod.getCode());
                shippingMethodDTO.setName(shippingMethod.getName());
                shippingMethodDTO.setDescription(shippingMethod.getDescription());
                shippingMethodDTO.setBaseFee(shippingMethod.getBaseFee());
            }
        }

        BigDecimal finalShippingFee = baseShippingFee.subtract(shippingDiscountAmount);
        if (finalShippingFee.compareTo(BigDecimal.ZERO) < 0) {
            finalShippingFee = BigDecimal.ZERO;
        }

        // Set final values
        response.setBaseShippingFee(baseShippingFee);
        response.setFinalShippingFee(finalShippingFee);
        response.setFinalTotalAmount(response.getTotalAfterProductDiscounts().add(finalShippingFee));
        response.setAppliedCoupons(appliedCoupons);
        response.setShippingMethod(shippingMethodDTO);
        response.setEstimatedDeliveryMinDate(estimatedMinDate);
        response.setEstimatedDeliveryMaxDate(estimatedMaxDate);

        return response;
    }

    /**
     * Calculates the total amount for a list of cart items.
     */
    private BigDecimal calculateCartTotal(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return cartItems.stream()
                .map(item -> item.getProduct().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Validates if a coupon can be applied.
     * 
     * @throws CouponValidationException if coupon is not valid
     */
    private void isCouponValid(Coupon coupon, BigDecimal orderValue, UUID userId) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // Check status
        if (coupon.getStatus() != Coupon.CouponStatus.ACTIVE) {
            throw new CouponValidationException.InactiveCouponException(coupon.getCode());
        }

        // Check date range
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new CouponValidationException.CouponNotStartedException(coupon.getCode());
        }

        if (coupon.getExpiryDate() != null && now.isAfter(coupon.getExpiryDate())) {
            throw new CouponValidationException.ExpiredCouponException(coupon.getCode());
        }

        // Check minimum order value
        if (orderValue.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new CouponValidationException.MinimumOrderValueException(
                coupon.getCode(), 
                coupon.getMinOrderValue().toString()
            );
        }

        // Check usage limit per user
        long usageCount = orderCouponRepository.countCouponUsageByUser(coupon.getId(), userId);
        if (usageCount >= coupon.getUsagePerUser()) {
            throw new CouponValidationException.UsageLimitExceededException(coupon.getCode());
        }
    }

    /**
     * Calculates discount amount for order coupons.
     */
    private BigDecimal calculateOrderDiscount(Coupon coupon, BigDecimal orderValue) {
        BigDecimal discountAmount;

        if (coupon.getDiscountType() == Coupon.DiscountType.FIXED) {
            discountAmount = coupon.getDiscountValue();
        } else { // PERCENT
            discountAmount = orderValue.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        }

        // Apply maximum discount limit if set
        if (coupon.getMaxDiscount() != null && discountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
            discountAmount = coupon.getMaxDiscount();
        }

        // Ensure discount doesn't exceed order value
        if (discountAmount.compareTo(orderValue) > 0) {
            discountAmount = orderValue;
        }

        return discountAmount;
    }

    /**
     * Calculates discount amount for shipping coupons.
     */
    private BigDecimal calculateShippingDiscount(Coupon coupon, BigDecimal shippingFee) {
        BigDecimal discountAmount;

        if (coupon.getDiscountType() == Coupon.DiscountType.FIXED) {
            discountAmount = coupon.getDiscountValue();
        } else { // PERCENT
            discountAmount = shippingFee.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        }

        // Apply maximum discount limit if set
        if (coupon.getMaxDiscount() != null && discountAmount.compareTo(coupon.getMaxDiscount()) > 0) {
            discountAmount = coupon.getMaxDiscount();
        }

        // Ensure discount doesn't exceed shipping fee
        if (discountAmount.compareTo(shippingFee) > 0) {
            discountAmount = shippingFee;
        }

        return discountAmount;
    }
}
