package com.shopcart.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopcart.dto.OrderResponse;
import com.shopcart.dto.PlaceOrderRequest;
import com.shopcart.entity.Address;
import com.shopcart.entity.CartItem;
import com.shopcart.entity.Coupon;
import com.shopcart.entity.Inventory;
import com.shopcart.entity.Order;
import com.shopcart.entity.OrderCoupon;
import com.shopcart.entity.OrderItem;
import com.shopcart.entity.OrderPayment;
import com.shopcart.entity.PaymentMethod;
import com.shopcart.entity.ShippingMethod;
import com.shopcart.exception.CouponException;
import com.shopcart.repository.AddressRepository;
import com.shopcart.repository.CartItemRepository;
import com.shopcart.repository.CouponRepository;
import com.shopcart.repository.InventoryRepository;
import com.shopcart.repository.OrderCouponRepository;
import com.shopcart.repository.OrderItemRepository;
import com.shopcart.repository.OrderPaymentRepository;
import com.shopcart.repository.OrderRepository;
import com.shopcart.repository.PaymentMethodRepository;
import com.shopcart.repository.ShippingMethodRepository;
import com.shopcart.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * Service implementation for order operations.
 *
 * <p>This service provides comprehensive order processing including validation,
 * coupon management, payment processing, and inventory management.</p>
 *
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-10
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderCouponRepository orderCouponRepository;
    private final OrderPaymentRepository orderPaymentRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final InventoryRepository inventoryRepository;
    private final CouponRepository couponRepository;
    private final ShippingMethodRepository shippingMethodRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    @Override
    public OrderResponse placeOrder(UUID userId, PlaceOrderRequest request) {
        // ============== STEP 1: VALIDATE USER & INPUT ============== 
        if (!userRepository.existsById(userId)) {
            throw new IllegalArgumentException("Người dùng không tồn tại với id: " + userId);
        }

        // Validate input data
        if (request.getShippingFullName() == null || request.getShippingFullName().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên người nhận không được để trống");
        }
        if (request.getShippingPhone() == null || request.getShippingPhone().trim().isEmpty()) {
            throw new IllegalArgumentException("Số điện thoại không được để trống");
        }

        // ============== STEP 2: GET & VALIDATE CART ITEMS ==============
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Không thể đặt hàng với giỏ hàng trống");
        }

        // ============== STEP 3: VALIDATE ADDRESS ==============
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ không tồn tại với id: " + request.getAddressId()));
        
        if (!address.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Địa chỉ không thuộc về người dùng");
        }

        // ============== STEP 4: VALIDATE SHIPPING METHOD ==============
        ShippingMethod shippingMethod = shippingMethodRepository.findById(request.getShippingMethodId())
                .orElseThrow(() -> new IllegalArgumentException("Phương thức vận chuyển không tồn tại với id: " + request.getShippingMethodId()));
        
        if (!shippingMethod.getIsActive()) {
            throw new IllegalArgumentException("Phương thức vận chuyển không hoạt động");
        }

        // ============== STEP 5: VALIDATE PAYMENT METHOD ==============
        PaymentMethod paymentMethod = paymentMethodRepository.findById(request.getPaymentMethodId())
                .orElseThrow(() -> new IllegalArgumentException("Phương thức thanh toán không tồn tại với id: " + request.getPaymentMethodId()));
        
        if (!paymentMethod.getIsActive()) {
            throw new IllegalArgumentException("Phương thức thanh toán không hoạt động");
        }

        // ============== STEP 6: CALCULATE SUBTOTAL ==============
        BigDecimal subtotal = calculateSubtotal(cartItems);
        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Tổng giá trị đơn hàng phải lớn hơn 0");
        }

        // ============== STEP 7: VALIDATE INVENTORY (CRITICAL) ==============
        validateInventoryAvailability(cartItems);

        // ============== STEP 8: CREATE ORDER ==============
        // Calculate shipping fee first so we can set initial finalPrice
        BigDecimal shippingFee = shippingMethod.getBaseFee();
        BigDecimal initialFinalPrice = subtotal.add(shippingFee); // Will be updated with discounts later
        
        Order order = createOrder(userId, address, shippingMethod, subtotal, request, shippingFee, initialFinalPrice);
        // IMPORTANT: Save order first so it gets an ID for child records (OrderCoupon, OrderPayment, etc.)
        orderRepository.save(order);

        // ============== STEP 9: PROCESS COUPONS ==============
        BigDecimal totalCouponDiscount = BigDecimal.ZERO;
        List<OrderResponse.CouponInfo> appliedCoupons = new ArrayList<>();
        
        if (request.getCouponIds() != null && !request.getCouponIds().isEmpty()) {
            totalCouponDiscount = processCoupons(order, cartItems, request.getCouponIds(), userId, subtotal, appliedCoupons);
        }

        // ============== STEP 10: CALCULATE FINAL PRICING ==============
        BigDecimal finalPrice = subtotal.add(shippingFee).subtract(totalCouponDiscount);

        // Ensure final price is not negative
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) {
            finalPrice = BigDecimal.ZERO;
        }

        // ============== STEP 11: UPDATE ORDER WITH PRICING ==============
        order.setDiscount(totalCouponDiscount);
        order.setCouponDiscountAmount(totalCouponDiscount);
        order.setShippingFee(shippingFee);
        order.setFinalPrice(finalPrice);
        orderRepository.save(order);

        // ============== STEP 12: CREATE ORDER ITEMS ==============
        List<OrderResponse.OrderItemResponse> orderItemResponses = createOrderItems(order, cartItems);

        // ============== STEP 13: CREATE ORDER PAYMENT RECORD ==============
        createOrderPayment(order, paymentMethod);

        // ============== STEP 14: UPDATE INVENTORY ==============
        updateInventoryForOrder(userId);

        // ============== STEP 15: CLEAR USER CART ==============
        clearUserCart(userId);

        // ============== STEP 16: RETURN RESPONSE ==============
        return buildOrderResponse(order, orderItemResponses, appliedCoupons, paymentMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal validateAndCalculateCouponDiscount(UUID couponId, UUID userId, BigDecimal orderValue) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new CouponException("Mã giảm giá không tồn tại"));

        // Check if coupon is active
        if (coupon.getStatus() != Coupon.CouponStatus.ACTIVE) {
            throw new CouponException("Mã giảm giá không hoạt động");
        }

        LocalDateTime now = LocalDateTime.now();
        
        // Check date validity
        if (coupon.getStartDate() != null && now.isBefore(coupon.getStartDate())) {
            throw new CouponException("Mã giảm giá chưa có hiệu lực");
        }
        
        if (coupon.getExpiryDate() != null && now.isAfter(coupon.getExpiryDate())) {
            throw new CouponException("Mã giảm giá đã hết hạn");
        }

        // Check minimum order value
        if (orderValue.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new CouponException("Giá trị đơn hàng không đạt giá trị tối thiểu. Yêu cầu: " + 
                    coupon.getMinOrderValue() + ", hiện tại: " + orderValue);
        }

        // Check usage limit for user
        long usageCount = orderCouponRepository.countCouponUsageByUser(couponId, userId);
        if (usageCount >= coupon.getUsagePerUser()) {
            throw new CouponException("Bạn đã vượt quá giới hạn sử dụng mã này (" + coupon.getUsagePerUser() + " lần)");
        }

        // Calculate discount
        BigDecimal discount = BigDecimal.ZERO;
        
        if (coupon.getDiscountType() == Coupon.DiscountType.FIXED) {
            discount = coupon.getDiscountValue();
        } else if (coupon.getDiscountType() == Coupon.DiscountType.PERCENT) {
            discount = orderValue.multiply(coupon.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            
            // Apply maximum discount if set
            if (coupon.getMaxDiscount() != null && discount.compareTo(coupon.getMaxDiscount()) > 0) {
                discount = coupon.getMaxDiscount();
            }
        }

        // Ensure discount doesn't exceed order value
        if (discount.compareTo(orderValue) > 0) {
            discount = orderValue;
        }

        return discount;
    }

    @Override
    public void updateInventoryForOrder(UUID userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        
        for (CartItem cartItem : cartItems) {
            Inventory inventory = inventoryRepository.findById(cartItem.getProduct().getId()).orElse(null);
            if (inventory == null) {
                throw new IllegalStateException(
                        "Kho hàng không tồn tại cho sản phẩm: " + cartItem.getProduct().getName());
            }
            
            if (inventory.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException(
                        "Sản phẩm '" + cartItem.getProduct().getName() + 
                        "' không đủ hàng. Có sẵn: " + inventory.getQuantity() + 
                        ", yêu cầu: " + cartItem.getQuantity());
            }
            
            // Update inventory
            inventory.setQuantity(inventory.getQuantity() - cartItem.getQuantity());
            inventoryRepository.save(inventory);
        }
    }

    @Override
    public int clearUserCart(UUID userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
        int clearedItemCount = cartItems.size();
        
        if (clearedItemCount > 0) {
            cartItemRepository.deleteAll(cartItems);
        }
        
        return clearedItemCount;
    }

    /**
     * Calculates the subtotal of cart items.
     */
    private BigDecimal calculateSubtotal(List<CartItem> cartItems) {
        return cartItems.stream()
                .map(item -> item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Validates that all products in the cart have sufficient inventory.
     * This check happens BEFORE creating the order to prevent race conditions.
     */
    private void validateInventoryAvailability(List<CartItem> cartItems) {
        for (CartItem cartItem : cartItems) {
            Inventory inventory = inventoryRepository.findById(cartItem.getProduct().getId())
                    .orElseThrow(() -> new IllegalStateException(
                            "Kho hàng không tồn tại cho sản phẩm: " + cartItem.getProduct().getName()));
            
            if (inventory.getQuantity() < cartItem.getQuantity()) {
                throw new IllegalStateException(
                        "Sản phẩm '" + cartItem.getProduct().getName() + 
                        "' không đủ hàng. Có sẵn: " + inventory.getQuantity() + 
                        ", yêu cầu: " + cartItem.getQuantity());
            }
        }
    }

    /**
     * Processes coupons and calculates total discount.
     * Each coupon is validated and applied sequentially.
     */
    private BigDecimal processCoupons(Order order, List<CartItem> cartItems, List<UUID> couponIds, 
                                     UUID userId, BigDecimal subtotal, List<OrderResponse.CouponInfo> appliedCoupons) {
        BigDecimal totalCouponDiscount = BigDecimal.ZERO;
        
        for (UUID couponId : couponIds) {
            try {
                // Validate coupon against the original subtotal
                BigDecimal discountAmount = validateAndCalculateCouponDiscount(couponId, userId, subtotal);
                
                // Ensure total discount doesn't exceed subtotal
                BigDecimal projectedTotal = totalCouponDiscount.add(discountAmount);
                if (projectedTotal.compareTo(subtotal) > 0) {
                    discountAmount = subtotal.subtract(totalCouponDiscount);
                }
                
                totalCouponDiscount = totalCouponDiscount.add(discountAmount);
                
                // Create order coupon record
                OrderCoupon orderCoupon = new OrderCoupon();
                orderCoupon.setOrderId(order.getId());
                orderCoupon.setCouponId(couponId);
                orderCoupon.setAppliedAmount(discountAmount);
                orderCouponRepository.save(orderCoupon);
                
                // Get coupon info for response
                Coupon coupon = couponRepository.findById(couponId)
                        .orElseThrow(() -> new CouponException("Mã giảm giá không tồn tại: " + couponId));
                
                appliedCoupons.add(OrderResponse.CouponInfo.builder()
                        .couponId(couponId)
                        .code(coupon.getCode())
                        .discountAmount(discountAmount)
                        .build());
                        
            } catch (CouponException e) {
                throw new IllegalArgumentException("Mã giảm giá không hợp lệ: " + couponId + " - " + e.getMessage());
            }
        }
        
        return totalCouponDiscount;
    }

    /**
     * Creates order items from cart items.
     */
    private List<OrderResponse.OrderItemResponse> createOrderItems(Order order, List<CartItem> cartItems) {
        List<OrderResponse.OrderItemResponse> orderItemResponses = new ArrayList<>();
        
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProduct().getId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItemRepository.save(orderItem);

            // Add to response
            BigDecimal itemTotal = cartItem.getProduct().getPrice()
                    .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
            orderItemResponses.add(OrderResponse.OrderItemResponse.builder()
                    .productId(cartItem.getProduct().getId())
                    .productName(cartItem.getProduct().getName())
                    .quantity(cartItem.getQuantity())
                    .price(cartItem.getProduct().getPrice())
                    .totalPrice(itemTotal)
                    .build());
        }
        
        return orderItemResponses;
    }

    /**
     * Creates the payment record for an order.
     */
    private void createOrderPayment(Order order, PaymentMethod paymentMethod) {
        OrderPayment orderPayment = new OrderPayment();
        orderPayment.setOrderId(order.getId());
        orderPayment.setMethod(1); // Legacy payment method code (required for database)
        orderPayment.setPaymentMethodId(paymentMethod.getId());
        orderPayment.setStatus(OrderPayment.PaymentStatus.PENDING);
        orderPaymentRepository.save(orderPayment);
    }

    private Order createOrder(UUID userId, Address address, ShippingMethod shippingMethod, BigDecimal subtotal, PlaceOrderRequest request, BigDecimal shippingFee, BigDecimal initialFinalPrice) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(address.getId());
        
        // Copy shipping information from address and request
        order.setShippingFullName(request.getShippingFullName()); // Use full name from request
        order.setShippingPhone(request.getShippingPhone()); // Use phone from request
        order.setShippingAddressLine(address.getAddressLine());
        order.setShippingCity(address.getCity());
        order.setShippingDistrict(address.getDistrict());
        order.setShippingWard(address.getWard());
        
        // Set shipping method information
        order.setShippingMethodId(shippingMethod.getId());
        order.setShippingMethodName(shippingMethod.getName());
        order.setEstimatedDeliveryMin(shippingMethod.getEstimatedDaysMin());
        order.setEstimatedDeliveryMax(shippingMethod.getEstimatedDaysMax());
        
        // Set pricing - IMPORTANT: Set all pricing fields to non-null values before first save
        order.setSubtotal(subtotal);
        order.setShippingFee(shippingFee);
        order.setDiscount(BigDecimal.ZERO); // Will be updated with coupon discounts
        order.setCouponDiscountAmount(BigDecimal.ZERO); // Will be updated with coupon discounts
        order.setFinalPrice(initialFinalPrice); // Will be updated with discounts
        
        // Set timestamps and status
        order.initializeDefaults();
        
        return order;
    }

    private OrderResponse buildOrderResponse(Order order, List<OrderResponse.OrderItemResponse> items, 
                                          List<OrderResponse.CouponInfo> coupons, PaymentMethod paymentMethod) {
        
        OrderResponse.ShippingInfo shippingInfo = OrderResponse.ShippingInfo.builder()
                .fullName(order.getShippingFullName())
                .phone(order.getShippingPhone())
                .addressLine(order.getShippingAddressLine())
                .city(order.getShippingCity())
                .district(order.getShippingDistrict())
                .ward(order.getShippingWard())
                .methodName(order.getShippingMethodName())
                .shippingFee(order.getShippingFee())
                .estimatedDeliveryMin(order.getEstimatedDeliveryMin())
                .estimatedDeliveryMax(order.getEstimatedDeliveryMax())
                .build();

        String paymentMethodName = paymentMethod != null ? paymentMethod.getName() : "Không xác định";
        OrderResponse.PaymentInfo paymentInfo = OrderResponse.PaymentInfo.builder()
                .methodName(paymentMethodName)
                .status("PENDING")
                .build();

        OrderResponse.PricingInfo pricingInfo = OrderResponse.PricingInfo.builder()
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .discount(order.getDiscount())
                .couponDiscount(order.getCouponDiscountAmount())
                .finalPrice(order.getFinalPrice())
                .build();

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus().toString())
                .shippingInfo(shippingInfo)
                .paymentInfo(paymentInfo)
                .items(items)
                .pricingInfo(pricingInfo)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .appliedCoupons(coupons)
                .build();
    }
}
