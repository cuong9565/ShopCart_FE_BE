package com.shopcart.service;

import com.shopcart.dto.AddToCartRequest;
import com.shopcart.dto.CartItemResponseDTO;
import com.shopcart.dto.CartPricingRequest;
import com.shopcart.dto.CartPricingResponse;
import com.shopcart.entity.*;
import com.shopcart.repository.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("CartService Tests")
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    // ── Mocks ────────────────────────────────────────────────────────────────
    @Mock private CartItemRepository       cartItemRepository;
    @Mock private UserRepository           userRepository;
    @Mock private ProductRepository        productRepository;
    @Mock private InventoryRepository      inventoryRepository;
    @Mock private ProductImageRepository   productImageRepository;
    @Mock private CouponRepository         couponRepository;
    @Mock private ShippingMethodRepository shippingMethodRepository;
    @Mock private OrderCouponRepository    orderCouponRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    // ── Fixtures chung ───────────────────────────────────────────────────────
    private UUID      userId;
    private UUID      productId;
    private User      user;
    private Product   product;
    private Inventory inventory;

    @BeforeEach
    void setUp() {
        userId    = UUID.randomUUID();
        productId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        product = new Product();
        product.setId(productId);
        product.setName("Áo thun");
        product.setPrice(new BigDecimal("100000"));
        product.setStatus("ACTIVE");
        product.setSlug("ao-thun");

        inventory = new Inventory();
        inventory.setQuantity(10);
    }

    // =========================================================================
    // HELPER – tránh lặp mock cho addToCart happy-path
    // =========================================================================

    /** Mock các bước chung khi thêm vào giỏ hàng */
    private void mockAddToCartBase(CartItem existingItem) {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(productId)).thenReturn(inventory);
        when(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(existingItem);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    /** Tạo AddToCartRequest nhanh */
    private AddToCartRequest cartRequest(int qty) {
        AddToCartRequest req = new AddToCartRequest();
        req.setProductId(productId);
        req.setQuantity(qty);
        return req;
    }

    /** Tạo Coupon ORDER hợp lệ nhanh */
    private Coupon buildOrderCoupon(String code,
                                    Coupon.DiscountType type,
                                    BigDecimal value,
                                    BigDecimal minOrder) {
        Coupon c = new Coupon();
        c.setId(UUID.randomUUID());
        c.setCode(code);
        c.setStatus(Coupon.CouponStatus.ACTIVE);
        c.setCouponScope(Coupon.CouponScope.ORDER);
        c.setDiscountType(type);
        c.setDiscountValue(value);
        c.setMinOrderValue(minOrder);
        c.setUsagePerUser(5);
        c.setStartDate(LocalDateTime.now().minusDays(1));
        c.setExpiryDate(LocalDateTime.now().plusDays(1));
        return c;
    }

    /** Mock calculateCartPricing base (user + cart items) */
    private void mockPricingBase(List<CartItem> items) {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(items);
    }

    // =========================================================================
    // ADD TO CART
    // =========================================================================

   @Test
@DisplayName("TC01 - Thêm sản phẩm mới thành công")
void testAddToCart_NewProduct() {
    mockAddToCartBase(null);
    ProductImage image = new ProductImage();
    image.setImageUrl("image.jpg");
    when(productImageRepository.findThumbnailByProductId(productId)).thenReturn(image);

    CartItemResponseDTO result = cartService.addToCart(userId, cartRequest(2));

    // --- Tăng cường Verify & Assert ---
    assertNotNull(result);
    assertEquals("image.jpg", result.getThumbnailImage());
    
    // Verify gọi vào tất cả repo liên quan
    verify(userRepository, times(1)).findById(userId);
    verify(productRepository, times(1)).findById(productId);
    verify(inventoryRepository, times(1)).findByProductId(productId);
    verify(productImageRepository, times(1)).findThumbnailByProductId(productId);
    verify(cartItemRepository, times(1)).save(any(CartItem.class));
}

    @Test
    @DisplayName("TC02 - Cộng dồn số lượng sản phẩm đã có (3 + 2 = 5)")
    void testAddToCart_ExistingProduct() {
        CartItem existing = new CartItem(user, product, 3);
        mockAddToCartBase(existing);

        CartItemResponseDTO result = cartService.addToCart(userId, cartRequest(2));

        assertEquals(5, result.getQuantity());
        assertEquals(0, result.getSubtotal().compareTo(new BigDecimal("500000")));
        verify(cartItemRepository).save(existing);
    }

    @Test
    @DisplayName("TC03 - User không tồn tại → IllegalArgumentException")
    void testAddToCart_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addToCart(userId, cartRequest(1)));

        assertTrue(ex.getMessage().contains("User not found"));
        verify(productRepository, never()).findById(any());
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC04 - Product không tồn tại → IllegalArgumentException")
    void testAddToCart_ProductNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addToCart(userId, cartRequest(1)));

        assertTrue(ex.getMessage().contains("Product not found"));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC05 - Product INACTIVE → IllegalArgumentException")
    void testAddToCart_ProductInactive() {
        product.setStatus("INACTIVE");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addToCart(userId, cartRequest(1)));

        assertTrue(ex.getMessage().contains("Product is not available"));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC06 - Số lượng vượt tồn kho → IllegalArgumentException")
    void testAddToCart_InsufficientInventory() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(inventoryRepository.findByProductId(productId)).thenReturn(inventory);
       

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.addToCart(userId, cartRequest(50))); // kho = 10

        assertTrue(ex.getMessage().contains("Insufficient inventory"));
        verify(cartItemRepository, never()).save(any());
    }

    // =========================================================================
    // UPDATE QUANTITY
    // =========================================================================

    @Test
@DisplayName("TC07 - Cập nhật số lượng thành công")
void testUpdateQuantity_Success() {
    CartItem item = new CartItem(user, product, 1);
    when(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(item);
    when(inventoryRepository.findByProductId(productId)).thenReturn(inventory);
    when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);

    cartService.updateProductQuantityFromCart(userId, productId, 5);

    // Dùng ArgumentCaptor để check sâu vào object được lưu
    org.mockito.ArgumentCaptor<CartItem> itemCaptor = org.mockito.ArgumentCaptor.forClass(CartItem.class);
    verify(cartItemRepository).save(itemCaptor.capture());
    
    CartItem savedItem = itemCaptor.getValue();
    assertEquals(5, savedItem.getQuantity());
    assertEquals(userId, savedItem.getUser().getId());
    assertEquals(productId, savedItem.getProduct().getId());
}

    @Test
    @DisplayName("TC08 - Cart item không tồn tại → IllegalArgumentException")
    void testUpdateQuantity_ItemNotFound() {
        when(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.updateProductQuantityFromCart(userId, productId, 2));

        assertTrue(ex.getMessage().contains("Cart item not found"));
        verify(cartItemRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC09 - Cập nhật vượt tồn kho → IllegalArgumentException")
    void testUpdateQuantity_InsufficientInventory() {
        CartItem item = new CartItem(user, product, 1);
        when(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(item);
        when(inventoryRepository.findByProductId(productId)).thenReturn(inventory);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.updateProductQuantityFromCart(userId, productId, 100));

        assertTrue(ex.getMessage().contains("Insufficient inventory"));
        verify(cartItemRepository, never()).save(any());
    }

    // =========================================================================
    // REMOVE FROM CART
    // =========================================================================

    @Test
    @DisplayName("TC10 - Xóa sản phẩm thành công → true")
    void testRemoveProduct_Success() {
        when(cartItemRepository.deleteByUserIdAndProductId(userId, productId)).thenReturn(1);

        assertTrue(cartService.removeProductFromCart(userId, productId));
        verify(cartItemRepository, times(1)).deleteByUserIdAndProductId(userId, productId);
    }

    @Test
    @DisplayName("TC11 - Xóa sản phẩm không tìm thấy → false")
    void testRemoveProduct_Failed() {
        when(cartItemRepository.deleteByUserIdAndProductId(userId, productId)).thenReturn(0);

        assertFalse(cartService.removeProductFromCart(userId, productId));
    }

    // =========================================================================
    // GET CART ITEMS
    // =========================================================================

    @Test
    @DisplayName("TC12 - Lấy danh sách cart thành công")
    void testGetCartItems() {
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(List.of(new CartItem(user, product, 2),
                                    new CartItem(user, product, 1)));

        List<CartItemResponseDTO> result = cartService.getCartItems(userId);

        assertEquals(2, result.size());
        verify(cartItemRepository).findByUserIdOrderByCreatedAtAsc(userId);
    }

    // =========================================================================
    // CART TOTAL
    // =========================================================================

@Test
@DisplayName("TC13 - Tính tổng tiền")
void testGetCartTotalAmount() {
    when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId))
            .thenReturn(List.of(new CartItem(user, product, 2)));

    BigDecimal total = cartService.getCartTotalAmount(userId);

    // Assert kĩ để phủ code mapping
    assertNotNull(total);
    assertTrue(total.compareTo(BigDecimal.ZERO) > 0);
    assertEquals(0, total.compareTo(new BigDecimal("200000")));
    
    verify(cartItemRepository).findByUserIdOrderByCreatedAtAsc(userId);
}

    @Test
    @DisplayName("TC14 - Giỏ hàng rỗng → tổng = 0")
    void testGetCartTotalAmount_Empty() {
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId))
                .thenReturn(List.of());

        assertEquals(BigDecimal.ZERO, cartService.getCartTotalAmount(userId));
    }

    // =========================================================================
    // CALCULATE CART PRICING
    // =========================================================================

    @Test
    @DisplayName("TC15 - Tính pricing không có coupon: 2 × 100000 = 200000")
    void testCalculateCartPricing_NoCoupon() {
        mockPricingBase(List.of(new CartItem(user, product, 2)));

        CartPricingResponse result =
                cartService.calculateCartPricing(userId, new CartPricingRequest());

        assertNotNull(result);
        assertEquals(0, result.getFinalTotalAmount().compareTo(new BigDecimal("200000")));
    }

    @Test
    @DisplayName("TC16 - User không tồn tại khi pricing → IllegalArgumentException")
    void testCalculateCartPricing_UserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.calculateCartPricing(userId, new CartPricingRequest()));

        assertTrue(ex.getMessage().contains("User not found"));
    }

   @Test
@DisplayName("TC17 - Coupon ORDER % hợp lệ")
void testCalculateCartPricing_OrderCoupon() {
    Coupon coupon = buildOrderCoupon("SALE10", Coupon.DiscountType.PERCENT, new BigDecimal("10"), BigDecimal.ZERO);
    mockPricingBase(List.of(new CartItem(user, product, 2)));
    when(couponRepository.findByCode("SALE10")).thenReturn(coupon);
    when(orderCouponRepository.countCouponUsageByUser(coupon.getId(), userId)).thenReturn(0L);

    cartService.calculateCartPricing(userId, new CartPricingRequest(List.of("SALE10"), null));

    // Verify các bước check điều kiện coupon
    verify(couponRepository).findByCode("SALE10");
    verify(orderCouponRepository).countCouponUsageByUser(coupon.getId(), userId);
    
    // Nếu có logic liên quan đến ngày tháng, verify thêm nếu có dùng Clock/Date
}
    @Test
    @DisplayName("TC18 - Coupon SHIPPING FIXED: 50000 - 30000 = 20000")
    void testCalculateCartPricing_ShippingCoupon() {
        UUID shippingId = UUID.randomUUID();

        ShippingMethod shipping = new ShippingMethod();
        shipping.setId(shippingId);
        shipping.setIsActive(true);
        shipping.setBaseFee(new BigDecimal("50000"));
        shipping.setEstimatedDaysMin(2);
        shipping.setEstimatedDaysMax(5);

        Coupon coupon = new Coupon();
        coupon.setId(UUID.randomUUID());
        coupon.setCode("SHIP");
        coupon.setStatus(Coupon.CouponStatus.ACTIVE);
        coupon.setCouponScope(Coupon.CouponScope.SHIPPING);
        coupon.setDiscountType(Coupon.DiscountType.FIXED);
        coupon.setDiscountValue(new BigDecimal("30000"));
        coupon.setMinOrderValue(BigDecimal.ZERO);
        coupon.setUsagePerUser(5);
        coupon.setStartDate(LocalDateTime.now().minusDays(1));
        coupon.setExpiryDate(LocalDateTime.now().plusDays(1));

        mockPricingBase(List.of(new CartItem(user, product, 2)));
        when(shippingMethodRepository.findById(shippingId)).thenReturn(Optional.of(shipping));
        when(couponRepository.findByCode("SHIP")).thenReturn(coupon);
        when(orderCouponRepository.countCouponUsageByUser(coupon.getId(), userId)).thenReturn(0L);

        CartPricingRequest req = new CartPricingRequest();
        req.setShippingMethodId(shippingId);
        req.setCouponCodes(List.of("SHIP"));

        CartPricingResponse result = cartService.calculateCartPricing(userId, req);

        assertNotNull(result);
        assertEquals(0, result.getFinalShippingFee().compareTo(new BigDecimal("20000")));
    }

    @Test
    @DisplayName("TC19 - Coupon hết hạn → không áp dụng")
    void testCalculateCartPricing_ExpiredCoupon() {
        Coupon coupon = new Coupon();
        coupon.setCode("EXPIRED");
        coupon.setStatus(Coupon.CouponStatus.ACTIVE);
        coupon.setCouponScope(Coupon.CouponScope.ORDER);
        coupon.setDiscountType(Coupon.DiscountType.FIXED);
        coupon.setDiscountValue(new BigDecimal("10000"));
        coupon.setMinOrderValue(BigDecimal.ZERO);
        coupon.setUsagePerUser(5);
        coupon.setStartDate(LocalDateTime.now().minusDays(5));
        coupon.setExpiryDate(LocalDateTime.now().minusDays(1)); // đã hết hạn

        mockPricingBase(List.of(new CartItem(user, product, 2)));
        when(couponRepository.findByCode("EXPIRED")).thenReturn(coupon);

        CartPricingRequest req = new CartPricingRequest();
        req.setCouponCodes(List.of("EXPIRED"));

        CartPricingResponse result = cartService.calculateCartPricing(userId, req);

        assertEquals(0, result.getAppliedCoupons().size());
    }

    @Test
    @DisplayName("TC20 - Coupon chưa tới ngày bắt đầu → không áp dụng")
    void testCalculateCartPricing_CouponNotStarted() {
        Coupon coupon = new Coupon();
        coupon.setCode("FUTURE");
        coupon.setStatus(Coupon.CouponStatus.ACTIVE);
        coupon.setCouponScope(Coupon.CouponScope.ORDER);
        coupon.setDiscountType(Coupon.DiscountType.FIXED);
        coupon.setDiscountValue(new BigDecimal("10000"));
        coupon.setMinOrderValue(BigDecimal.ZERO);
        coupon.setUsagePerUser(5);
        coupon.setStartDate(LocalDateTime.now().plusDays(2)); // chưa bắt đầu
        coupon.setExpiryDate(LocalDateTime.now().plusDays(5));

        mockPricingBase(List.of(new CartItem(user, product, 2)));
        when(couponRepository.findByCode("FUTURE")).thenReturn(coupon);

        CartPricingRequest req = new CartPricingRequest();
        req.setCouponCodes(List.of("FUTURE"));

        CartPricingResponse result = cartService.calculateCartPricing(userId, req);

        assertEquals(0, result.getAppliedCoupons().size());
    }

    @Test
    @DisplayName("TC21 - Coupon INACTIVE → không áp dụng")
    void testCalculateCartPricing_CouponInactive() {
        Coupon coupon = new Coupon();
        coupon.setCode("OFF");
        coupon.setStatus(Coupon.CouponStatus.INACTIVE);

        mockPricingBase(List.of(new CartItem(user, product, 2)));
        when(couponRepository.findByCode("OFF")).thenReturn(coupon);

        CartPricingRequest req = new CartPricingRequest();
        req.setCouponCodes(List.of("OFF"));

        CartPricingResponse result = cartService.calculateCartPricing(userId, req);

        assertEquals(0, result.getAppliedCoupons().size());
    }

    @Test
    @DisplayName("TC22 - Chưa đạt giá trị đơn tối thiểu → coupon không áp dụng")
    void testCalculateCartPricing_MinOrderValueFailed() {
        // item: 1 × 100000 = 100000 < minOrderValue 500000
        Coupon coupon = buildOrderCoupon("MIN500",
                Coupon.DiscountType.FIXED, new BigDecimal("10000"), new BigDecimal("500000"));

        mockPricingBase(List.of(new CartItem(user, product, 1)));
        when(couponRepository.findByCode("MIN500")).thenReturn(coupon);

        CartPricingRequest req = new CartPricingRequest();
        req.setCouponCodes(List.of("MIN500"));

        CartPricingResponse result = cartService.calculateCartPricing(userId, req);

        assertTrue(result.getAppliedCoupons().isEmpty());
    }

    @Test
    @DisplayName("TC24 - Coupon % có maxDiscount: 50% × 1000000 = 500000 → bị cap tại 100000")
    void testCalculateCartPricing_PercentCouponWithMaxDiscount() {
        // 10 items × 100000 = 1000000; 50% = 500000 nhưng maxDiscount = 100000
        Coupon coupon = buildOrderCoupon("MAXSALE",
                Coupon.DiscountType.PERCENT, new BigDecimal("50"), BigDecimal.ZERO);
        coupon.setMaxDiscount(new BigDecimal("100000"));

        mockPricingBase(List.of(new CartItem(user, product, 10)));
        when(couponRepository.findByCode("MAXSALE")).thenReturn(coupon);
        when(orderCouponRepository.countCouponUsageByUser(coupon.getId(), userId)).thenReturn(0L);

        CartPricingRequest req = new CartPricingRequest();
        req.setCouponCodes(List.of("MAXSALE"));

        CartPricingResponse result = cartService.calculateCartPricing(userId, req);

        assertNotNull(result);
        // totalAfterDiscount = 1000000 - 100000 = 900000
        assertEquals(0, result.getTotalAfterProductDiscounts()
                               .compareTo(new BigDecimal("900000")));
    }

    @Test
    @DisplayName("TC25 - Shipping method INACTIVE → phí ship = 0")
    void testCalculateCartPricing_ShippingInactive() {
        UUID shippingId = UUID.randomUUID();

        ShippingMethod shipping = new ShippingMethod();
        shipping.setId(shippingId);
        shipping.setIsActive(false);
        shipping.setBaseFee(new BigDecimal("50000"));

        mockPricingBase(List.of(new CartItem(user, product, 2)));
        when(shippingMethodRepository.findById(shippingId)).thenReturn(Optional.of(shipping));

        CartPricingRequest req = new CartPricingRequest();
        req.setShippingMethodId(shippingId);

        CartPricingResponse result = cartService.calculateCartPricing(userId, req);

        assertEquals(BigDecimal.ZERO, result.getFinalShippingFee());
    }

    @Test
    @DisplayName("TC26 - Shipping method không tồn tại → IllegalArgumentException")
    void testCalculateCartPricing_ShippingNotFound() {
        UUID shippingId = UUID.randomUUID();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId)).thenReturn(List.of());
        when(shippingMethodRepository.findById(shippingId)).thenReturn(Optional.empty());

        CartPricingRequest req = new CartPricingRequest();
        req.setShippingMethodId(shippingId);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> cartService.calculateCartPricing(userId, req));

        assertTrue(ex.getMessage().contains("Shipping method not found"));
    }

    @Test
    @DisplayName("TC27 - Coupon code không tồn tại trong DB → bỏ qua, appliedCoupons rỗng")
    void testCalculateCartPricing_CouponNull() {
        mockPricingBase(List.of(new CartItem(user, product, 1)));
        when(couponRepository.findByCode("ABC")).thenReturn(null);

        CartPricingRequest req = new CartPricingRequest();
        req.setCouponCodes(List.of("ABC"));

        CartPricingResponse result = cartService.calculateCartPricing(userId, req);

        assertTrue(result.getAppliedCoupons().isEmpty());
    }

    @Test
    @DisplayName("TC28 - Shipping coupon giảm vượt phí ship → phí ship = 0")
    void testCalculateCartPricing_ShippingDiscountGreaterThanFee() {
        // baseFee = 10000, discount = 50000 → min(10000, 50000) = 10000 → fee = 0
        UUID shippingId = UUID.randomUUID();

        ShippingMethod shipping = new ShippingMethod();
        shipping.setId(shippingId);
        shipping.setIsActive(true);
        shipping.setBaseFee(new BigDecimal("10000"));

        Coupon coupon = new Coupon();
        coupon.setId(UUID.randomUUID());
        coupon.setCode("SHIPFREE");
        coupon.setStatus(Coupon.CouponStatus.ACTIVE);
        coupon.setCouponScope(Coupon.CouponScope.SHIPPING);
        coupon.setDiscountType(Coupon.DiscountType.FIXED);
        coupon.setDiscountValue(new BigDecimal("50000"));
        coupon.setMinOrderValue(BigDecimal.ZERO);
        coupon.setUsagePerUser(5);
        coupon.setStartDate(LocalDateTime.now().minusDays(1));
        coupon.setExpiryDate(LocalDateTime.now().plusDays(1));

        mockPricingBase(List.of(new CartItem(user, product, 1)));
        when(shippingMethodRepository.findById(shippingId)).thenReturn(Optional.of(shipping));
        when(couponRepository.findByCode("SHIPFREE")).thenReturn(coupon);
        when(orderCouponRepository.countCouponUsageByUser(coupon.getId(), userId)).thenReturn(0L);

        CartPricingRequest req = new CartPricingRequest();
        req.setShippingMethodId(shippingId);
        req.setCouponCodes(List.of("SHIPFREE"));

        CartPricingResponse result = cartService.calculateCartPricing(userId, req);

        assertEquals(BigDecimal.ZERO, result.getFinalShippingFee());
    }
}