package com.shopcart.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shopcart.dto.OrderResponse;
import com.shopcart.dto.PlaceOrderRequest;
import com.shopcart.entity.Address;
import com.shopcart.entity.CartItem;
import com.shopcart.entity.Coupon;
import com.shopcart.entity.Inventory;
import com.shopcart.entity.Order;
import com.shopcart.entity.OrderPayment;
import com.shopcart.entity.PaymentMethod;
import com.shopcart.entity.Product;
import com.shopcart.entity.ShippingMethod;
import com.shopcart.entity.User;
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

/**
 * Comprehensive unit tests for OrderService implementation.
 * 
 * <p>Test cases follow AAA pattern (Arrange-Act-Assert) and cover:
 * <ul>
 *   <li>Successful order placement</li>
 *   <li>Order placement with valid coupons</li>
 *   <li>Missing shipping address validation</li>
 *   <li>Insufficient inventory handling</li>
 *   <li>Expired coupon validation</li>
 * </ul>
 * </p>
 * 
 * <p>Tests use Mockito for mocking dependencies and AssertJ for fluent assertions.
 * All test names clearly describe the scenario and expected behavior.</p>
 * 
 * @author ShopCart Team
 * @version 1.0
 * @since 2026-05-11
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OrderService Tests")
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderItemRepository orderItemRepository;
    
    @Mock
    private OrderCouponRepository orderCouponRepository;
    
    @Mock
    private OrderPaymentRepository orderPaymentRepository;
    
    @Mock
    private CartItemRepository cartItemRepository;
    
    @Mock
    private UserRepository userRepository;
    
    @Mock
    private AddressRepository addressRepository;
    
    @Mock
    private InventoryRepository inventoryRepository;
    
    @Mock
    private CouponRepository couponRepository;
    
    @Mock
    private ShippingMethodRepository shippingMethodRepository;
    
    @Mock
    private PaymentMethodRepository paymentMethodRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private UUID userId;
    private UUID addressId;
    private UUID shippingMethodId;
    private UUID paymentMethodId;
    private UUID productId;
    private UUID couponId;
    private PlaceOrderRequest placeOrderRequest;
    private User testUser;
    private Address testAddress;
    private ShippingMethod testShippingMethod;
    private PaymentMethod testPaymentMethod;
    private Product testProduct;
    private CartItem testCartItem;
    private Inventory testInventory;
    private Coupon testCoupon;

    @BeforeEach
    void setUp() {
        // Initialize test data
        userId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        shippingMethodId = UUID.randomUUID();
        paymentMethodId = UUID.randomUUID();
        productId = UUID.randomUUID();
        couponId = UUID.randomUUID();

        // Create test entities
        testUser = new User();
        testUser.setId(userId);

        testAddress = new Address();
        testAddress.setId(addressId);
        testAddress.setUserId(userId);
        testAddress.setAddressLine("123 Test Street");
        testAddress.setCity("Test City");
        testAddress.setDistrict("Test District");
        testAddress.setWard("Test Ward");

        testShippingMethod = new ShippingMethod();
        testShippingMethod.setId(shippingMethodId);
        testShippingMethod.setName("Standard Shipping");
        testShippingMethod.setBaseFee(new BigDecimal("10.00"));
        testShippingMethod.setIsActive(true);
        testShippingMethod.setEstimatedDaysMin(3);
        testShippingMethod.setEstimatedDaysMax(5);

        testPaymentMethod = new PaymentMethod();
        testPaymentMethod.setId(paymentMethodId);
        testPaymentMethod.setName("Credit Card");
        testPaymentMethod.setIsActive(true);

        testProduct = new Product();
        testProduct.setId(productId);
        testProduct.setName("Test Product");
        testProduct.setPrice(new BigDecimal("100.00"));

        testCartItem = new CartItem();
        testCartItem.setId(UUID.randomUUID());
        testCartItem.setUser(testUser);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);
        testCartItem.setCreatedAt(LocalDateTime.now());

        testInventory = new Inventory();
        testInventory.setProductId(productId);
        testInventory.setQuantity(10);

        testCoupon = new Coupon();
        testCoupon.setId(couponId);
        testCoupon.setCode("TEST10");
        testCoupon.setDiscountValue(new BigDecimal("10.00"));
        testCoupon.setDiscountType(Coupon.DiscountType.FIXED);
        testCoupon.setMinOrderValue(new BigDecimal("50.00"));
        testCoupon.setUsagePerUser(1);
        testCoupon.setStatus(Coupon.CouponStatus.ACTIVE);
        testCoupon.setStartDate(LocalDateTime.now().minusDays(1));
        testCoupon.setExpiryDate(LocalDateTime.now().plusDays(7));

        // Create test request
        placeOrderRequest = new PlaceOrderRequest();
        placeOrderRequest.setAddressId(addressId);
        placeOrderRequest.setShippingMethodId(shippingMethodId);
        placeOrderRequest.setPaymentMethodId(paymentMethodId);
        placeOrderRequest.setShippingFullName("John Doe");
        placeOrderRequest.setShippingPhone("1234567890");
        placeOrderRequest.setCouponIds(new ArrayList<>());
    }

    @Test
    @DisplayName("placeOrder_ShouldCreateOrderSuccessfully_WhenAllValidationsPass")
    void placeOrder_ShouldCreateOrderSuccessfully_WhenAllValidationsPass() {
        // Arrange
        List<CartItem> cartItems = List.of(testCartItem);
        
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(cartItems);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(testAddress));
        when(shippingMethodRepository.findById(shippingMethodId)).thenReturn(Optional.of(testShippingMethod));
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(testInventory));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });
        when(orderItemRepository.save(any())).thenReturn(new com.shopcart.entity.OrderItem());
        when(orderPaymentRepository.save(any())).thenReturn(new OrderPayment());

        // Act
        OrderResponse result = orderService.placeOrder(userId, placeOrderRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPricingInfo().getSubtotal()).isEqualTo(new BigDecimal("200.00"));
        assertThat(result.getPricingInfo().getShippingFee()).isEqualTo(new BigDecimal("10.00"));
        assertThat(result.getPricingInfo().getFinalPrice()).isEqualTo(new BigDecimal("210.00"));
        assertThat(result.getShippingInfo().getFullName()).isEqualTo("John Doe");
        assertThat(result.getShippingInfo().getPhone()).isEqualTo("1234567890");

        // Verify interactions
        verify(userRepository).existsById(userId);
        verify(cartItemRepository, times(3)).findByUserIdOrderByCreatedAtAsc(userId);
        verify(addressRepository).findById(addressId);
        verify(shippingMethodRepository).findById(shippingMethodId);
        verify(paymentMethodRepository).findById(paymentMethodId);
        verify(inventoryRepository, times(2)).findById(productId);
        verify(orderRepository, times(2)).save(any(Order.class));
        verify(orderItemRepository).save(any());
        verify(orderPaymentRepository).save(any());
        verify(inventoryRepository).save(testInventory);
        verify(cartItemRepository).deleteAll(cartItems);
    }

    @Test
    @DisplayName("placeOrder_ShouldApplyCouponSuccessfully_WhenValidCouponProvided")
    void placeOrder_ShouldApplyCouponSuccessfully_WhenValidCouponProvided() {
        // Arrange
        placeOrderRequest.setCouponIds(List.of(couponId));
        List<CartItem> cartItems = List.of(testCartItem);
        
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(cartItems);
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(testAddress));
        when(shippingMethodRepository.findById(shippingMethodId)).thenReturn(Optional.of(testShippingMethod));
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(testInventory));
        when(couponRepository.findById(couponId)).thenReturn(Optional.of(testCoupon));
        when(orderCouponRepository.countCouponUsageByUser(couponId, userId)).thenReturn(0L);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(UUID.randomUUID());
            return order;
        });
        when(orderItemRepository.save(any())).thenReturn(new com.shopcart.entity.OrderItem());
        when(orderPaymentRepository.save(any())).thenReturn(new OrderPayment());

        // Act
        OrderResponse result = orderService.placeOrder(userId, placeOrderRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getPricingInfo().getSubtotal()).isEqualTo(new BigDecimal("200.00"));
        assertThat(result.getPricingInfo().getShippingFee()).isEqualTo(new BigDecimal("10.00"));
        assertThat(result.getPricingInfo().getCouponDiscount()).isEqualTo(new BigDecimal("10.00"));
        assertThat(result.getPricingInfo().getFinalPrice()).isEqualTo(new BigDecimal("200.00"));
        assertThat(result.getAppliedCoupons()).hasSize(1);
        assertThat(result.getAppliedCoupons().get(0).getCouponId()).isEqualTo(couponId);
        assertThat(result.getAppliedCoupons().get(0).getDiscountAmount()).isEqualTo(new BigDecimal("10.00"));

        // Verify coupon processing
        verify(couponRepository).findById(couponId);
        verify(orderCouponRepository).countCouponUsageByUser(couponId, userId);
        verify(cartItemRepository, times(3)).findByUserIdOrderByCreatedAtAsc(userId);
        verify(inventoryRepository, times(2)).findById(productId);
    }

    @Test
    @DisplayName("placeOrder_ShouldThrowException_WhenShippingAddressIsMissing")
    void placeOrder_ShouldThrowException_WhenShippingAddressIsMissing() {
        // Arrange
        placeOrderRequest.setAddressId(null);
        
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(List.of(testCartItem));

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(userId, placeOrderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Địa chỉ không tồn tại với id: " + null);

        // Verify no order creation
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any());
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder_ShouldThrowException_WhenShippingFullNameIsEmpty")
    void placeOrder_ShouldThrowException_WhenShippingFullNameIsEmpty() {
        // Arrange
        placeOrderRequest.setShippingFullName("");
        
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(userId, placeOrderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tên người nhận không được để trống");

        // Verify no order creation
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any());
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder_ShouldThrowException_WhenShippingPhoneIsEmpty")
    void placeOrder_ShouldThrowException_WhenShippingPhoneIsEmpty() {
        // Arrange
        placeOrderRequest.setShippingPhone("");
        
        when(userRepository.existsById(userId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(userId, placeOrderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Số điện thoại không được để trống");

        // Verify no order creation
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any());
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder_ShouldThrowException_WhenAddressDoesNotExist")
    void placeOrder_ShouldThrowException_WhenAddressDoesNotExist() {
        // Arrange
        UUID nonExistentAddressId = UUID.randomUUID();
        placeOrderRequest.setAddressId(nonExistentAddressId);
        
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(List.of(testCartItem));
        when(addressRepository.findById(nonExistentAddressId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(userId, placeOrderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Địa chỉ không tồn tại với id: " + nonExistentAddressId);

        // Verify no order creation
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any());
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder_ShouldThrowException_WhenAddressDoesNotBelongToUser")
    void placeOrder_ShouldThrowException_WhenAddressDoesNotBelongToUser() {
        // Arrange
        UUID otherUserId = UUID.randomUUID();
        testAddress.setUserId(otherUserId);
        
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(List.of(testCartItem));
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(testAddress));

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(userId, placeOrderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Địa chỉ không thuộc về người dùng");

        // Verify no order creation
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any());
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder_ShouldThrowException_WhenProductInventoryIsInsufficient")
    void placeOrder_ShouldThrowException_WhenProductInventoryIsInsufficient() {
        // Arrange
        testInventory.setQuantity(1); // Less than requested quantity (2)
        
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(List.of(testCartItem));
        when(addressRepository.findById(addressId)).thenReturn(Optional.of(testAddress));
        when(shippingMethodRepository.findById(shippingMethodId)).thenReturn(Optional.of(testShippingMethod));
        when(paymentMethodRepository.findById(paymentMethodId)).thenReturn(Optional.of(testPaymentMethod));
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(testInventory));

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(userId, placeOrderRequest))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Sản phẩm 'Test Product' không đủ hàng")
                .hasMessageContaining("Có sẵn: 1, yêu cầu: 2");

        // Verify no order creation
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any());
        verify(orderPaymentRepository, never()).save(any());
        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder_ShouldThrowException_WhenCartIsEmpty")
    void placeOrder_ShouldThrowException_WhenCartIsEmpty() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(true);
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(new ArrayList<>());

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(userId, placeOrderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Không thể đặt hàng với giỏ hàng trống");

        // Verify no order creation
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).save(any());
        verify(orderPaymentRepository, never()).save(any());
    }

    @Test
    @DisplayName("placeOrder_ShouldThrowException_WhenUserDoesNotExist")
    void placeOrder_ShouldThrowException_WhenUserDoesNotExist() {
        // Arrange
        when(userRepository.existsById(userId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> orderService.placeOrder(userId, placeOrderRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Người dùng không tồn tại với id: " + userId);

        // Verify no further processing
        verify(cartItemRepository, never()).findByUserIdOrderByCreatedAtAsc(any());
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    @DisplayName("validateAndCalculateCouponDiscount_ShouldReturnDiscount_WhenCouponIsValid")
    void validateAndCalculateCouponDiscount_ShouldReturnDiscount_WhenCouponIsValid() {
        // Arrange
        BigDecimal orderValue = new BigDecimal("200.00");
        
        when(orderCouponRepository.countCouponUsageByUser(couponId, userId)).thenReturn(0L);

        // Act
        BigDecimal result = orderService.validateAndCalculateCouponDiscount(testCoupon, userId, orderValue);

        // Assert
        assertThat(result).isEqualTo(new BigDecimal("10.00"));
        verify(orderCouponRepository).countCouponUsageByUser(couponId, userId);
    }

    @Test
    @DisplayName("validateAndCalculateCouponDiscount_ShouldThrowException_WhenCouponIsExpired")
    void validateAndCalculateCouponDiscount_ShouldThrowException_WhenCouponIsExpired() {
        // Arrange
        testCoupon.setExpiryDate(LocalDateTime.now().minusDays(1)); // Expired yesterday
        BigDecimal orderValue = new BigDecimal("200.00");

        // Act & Assert
        assertThatThrownBy(() -> orderService.validateAndCalculateCouponDiscount(testCoupon, userId, orderValue))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining("Mã giảm giá đã hết hạn");

        verify(orderCouponRepository, never()).countCouponUsageByUser(any(), any());
    }

    @Test
    @DisplayName("validateAndCalculateCouponDiscount_ShouldThrowException_WhenCouponIsInactive")
    void validateAndCalculateCouponDiscount_ShouldThrowException_WhenCouponIsInactive() {
        // Arrange
        testCoupon.setStatus(Coupon.CouponStatus.INACTIVE);
        BigDecimal orderValue = new BigDecimal("200.00");

        // Act & Assert
        assertThatThrownBy(() -> orderService.validateAndCalculateCouponDiscount(testCoupon, userId, orderValue))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining("Mã giảm giá không hoạt động");

        verify(orderCouponRepository, never()).countCouponUsageByUser(any(), any());
    }

    @Test
    @DisplayName("validateAndCalculateCouponDiscount_ShouldThrowException_WhenCouponNotStarted")
    void validateAndCalculateCouponDiscount_ShouldThrowException_WhenCouponNotStarted() {
        // Arrange
        testCoupon.setStartDate(LocalDateTime.now().plusDays(1)); // Starts tomorrow
        BigDecimal orderValue = new BigDecimal("200.00");

        // Act & Assert
        assertThatThrownBy(() -> orderService.validateAndCalculateCouponDiscount(testCoupon, userId, orderValue))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining("Mã giảm giá chưa có hiệu lực");

        verify(orderCouponRepository, never()).countCouponUsageByUser(any(), any());
    }

    @Test
    @DisplayName("validateAndCalculateCouponDiscount_ShouldThrowException_WhenOrderValueBelowMinimum")
    void validateAndCalculateCouponDiscount_ShouldThrowException_WhenOrderValueBelowMinimum() {
        // Arrange
        testCoupon.setMinOrderValue(new BigDecimal("150.00"));
        BigDecimal orderValue = new BigDecimal("100.00"); // Below minimum

        // Act & Assert
        assertThatThrownBy(() -> orderService.validateAndCalculateCouponDiscount(testCoupon, userId, orderValue))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining("Giá trị đơn hàng không đạt giá trị tối thiểu")
                .hasMessageContaining("Yêu cầu: 150.00, hiện tại: 100.00");

        verify(orderCouponRepository, never()).countCouponUsageByUser(any(), any());
    }

    @Test
    @DisplayName("validateAndCalculateCouponDiscount_ShouldThrowException_WhenUsageLimitExceeded")
    void validateAndCalculateCouponDiscount_ShouldThrowException_WhenUsageLimitExceeded() {
        // Arrange
        testCoupon.setUsagePerUser(2);
        BigDecimal orderValue = new BigDecimal("200.00");
        
        when(orderCouponRepository.countCouponUsageByUser(couponId, userId)).thenReturn(2L); // Already used 2 times

        // Act & Assert
        assertThatThrownBy(() -> orderService.validateAndCalculateCouponDiscount(testCoupon, userId, orderValue))
                .isInstanceOf(CouponException.class)
                .hasMessageContaining("Bạn đã vượt quá giới hạn sử dụng mã này (2 lần)");

        verify(orderCouponRepository).countCouponUsageByUser(couponId, userId);
    }

    @Test
    @DisplayName("validateAndCalculateCouponDiscount_ShouldCalculatePercentageDiscount_WhenCouponIsPercentageType")
    void validateAndCalculateCouponDiscount_ShouldCalculatePercentageDiscount_WhenCouponIsPercentageType() {
        // Arrange
        testCoupon.setDiscountType(Coupon.DiscountType.PERCENT);
        testCoupon.setDiscountValue(new BigDecimal("10")); // 10%
        testCoupon.setMaxDiscount(new BigDecimal("25.00")); // Max discount 25
        BigDecimal orderValue = new BigDecimal("200.00");
        
        when(orderCouponRepository.countCouponUsageByUser(couponId, userId)).thenReturn(0L);

        // Act
        BigDecimal result = orderService.validateAndCalculateCouponDiscount(testCoupon, userId, orderValue);

        // Assert
        assertThat(result).isEqualTo(new BigDecimal("20.00")); // 10% of 200 = 20
        verify(orderCouponRepository).countCouponUsageByUser(couponId, userId);
    }

    @Test
    @DisplayName("validateAndCalculateCouponDiscount_ShouldApplyMaxDiscount_WhenPercentageExceedsMaximum")
    void validateAndCalculateCouponDiscount_ShouldApplyMaxDiscount_WhenPercentageExceedsMaximum() {
        // Arrange
        testCoupon.setDiscountType(Coupon.DiscountType.PERCENT);
        testCoupon.setDiscountValue(new BigDecimal("20")); // 20%
        testCoupon.setMaxDiscount(new BigDecimal("15.00")); // Max discount 15
        BigDecimal orderValue = new BigDecimal("100.00"); // 20% of 100 = 20, but max is 15
        
        when(orderCouponRepository.countCouponUsageByUser(couponId, userId)).thenReturn(0L);

        // Act
        BigDecimal result = orderService.validateAndCalculateCouponDiscount(testCoupon, userId, orderValue);

        // Assert
        assertThat(result).isEqualTo(new BigDecimal("15.00")); // Max discount applied
        verify(orderCouponRepository).countCouponUsageByUser(couponId, userId);
    }

    @Test
    @DisplayName("validateAndCalculateCouponDiscount_ShouldCapDiscountAtOrderValue_WhenDiscountExceedsOrderValue")
    void validateAndCalculateCouponDiscount_ShouldCapDiscountAtOrderValue_WhenDiscountExceedsOrderValue() {
        // Arrange
        testCoupon.setDiscountValue(new BigDecimal("150.00")); // Fixed discount higher than order value
        BigDecimal orderValue = new BigDecimal("100.00");
        
        when(orderCouponRepository.countCouponUsageByUser(couponId, userId)).thenReturn(0L);

        // Act
        BigDecimal result = orderService.validateAndCalculateCouponDiscount(testCoupon, userId, orderValue);

        // Assert
        assertThat(result).isEqualTo(new BigDecimal("100.00")); // Capped at order value
        verify(orderCouponRepository).countCouponUsageByUser(couponId, userId);
    }

    @Test
    @DisplayName("updateInventoryForOrder_ShouldUpdateInventorySuccessfully_WhenSufficientStock")
    void updateInventoryForOrder_ShouldUpdateInventorySuccessfully_WhenSufficientStock() {
        // Arrange
        List<CartItem> cartItems = List.of(testCartItem);
        
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(cartItems);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(testInventory));

        // Act
        orderService.updateInventoryForOrder(userId);

        // Assert
        assertThat(testInventory.getQuantity()).isEqualTo(8); // 10 - 2 = 8
        verify(inventoryRepository).save(testInventory);
    }

    @Test
    @DisplayName("updateInventoryForOrder_ShouldThrowException_WhenInventoryDoesNotExist")
    void updateInventoryForOrder_ShouldThrowException_WhenInventoryDoesNotExist() {
        // Arrange
        List<CartItem> cartItems = List.of(testCartItem);
        
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(cartItems);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> orderService.updateInventoryForOrder(userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Kho hàng không tồn tại cho sản phẩm: Test Product");

        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateInventoryForOrder_ShouldThrowException_WhenInsufficientInventory")
    void updateInventoryForOrder_ShouldThrowException_WhenInsufficientInventory() {
        // Arrange
        testInventory.setQuantity(1); // Less than requested quantity (2)
        List<CartItem> cartItems = List.of(testCartItem);
        
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(cartItems);
        when(inventoryRepository.findById(productId)).thenReturn(Optional.of(testInventory));

        // Act & Assert
        assertThatThrownBy(() -> orderService.updateInventoryForOrder(userId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Sản phẩm 'Test Product' không đủ hàng")
                .hasMessageContaining("Có sẵn: 1, yêu cầu: 2");

        verify(inventoryRepository, never()).save(any());
    }

    @Test
    @DisplayName("clearUserCart_ShouldReturnItemCount_WhenCartHasItems")
    void clearUserCart_ShouldReturnItemCount_WhenCartHasItems() {
        // Arrange
        List<CartItem> cartItems = List.of(testCartItem);
        
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(cartItems);
        doNothing().when(cartItemRepository).deleteAll(cartItems);

        // Act
        int result = orderService.clearUserCart(userId);

        // Assert
        assertThat(result).isEqualTo(1);
        verify(cartItemRepository).deleteAll(cartItems);
    }

    @Test
    @DisplayName("clearUserCart_ShouldReturnZero_WhenCartIsEmpty")
    void clearUserCart_ShouldReturnZero_WhenCartIsEmpty() {
        // Arrange
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(new ArrayList<>());

        // Act
        int result = orderService.clearUserCart(userId);

        // Assert
        assertThat(result).isEqualTo(0);
        verify(cartItemRepository, never()).deleteAll(any());
    }
}
