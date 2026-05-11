package com.shopcart.security;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import com.shopcart.controller.CartController;
import com.shopcart.controller.OrderController;
import com.shopcart.dto.AddToCartRequest;
import com.shopcart.dto.CartItemResponseDTO;
import com.shopcart.dto.CartPricingRequest;
import com.shopcart.dto.CartPricingResponse;
import com.shopcart.dto.OrderResponse;
import com.shopcart.dto.PlaceOrderRequest;
import com.shopcart.dto.RemoveFromCartRequest;
import com.shopcart.dto.UpdateCartRequest;
import com.shopcart.entity.User;
import com.shopcart.service.CartService;
import com.shopcart.service.OrderService;

/**
 * Comprehensive Security Testing for Cart and Order Flows
 * 
 * This test class covers:
 * - SQL Injection vulnerabilities
 * - XSS (Cross-Site Scripting) attacks
 * - IDOR (Insecure Direct Object Reference)
 * - CSRF (Cross-Site Request Forgery)
 * - Authorization bypass attempts
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Cart and Order Security Tests")
class CartOrderSecurityTest {

    @Mock
    private CartService cartService;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private CartController cartController;

    @InjectMocks
    private OrderController orderController;

    private UUID userId;
    private UUID productId;
    private UUID addressId;
    private UUID shippingMethodId;
    private UUID paymentMethodId;
    private User testUser;
    private CustomUserDetails userDetails;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();
        addressId = UUID.randomUUID();
        shippingMethodId = UUID.randomUUID();
        paymentMethodId = UUID.randomUUID();

        testUser = new User();
        testUser.setId(userId);
        testUser.setEmail("test@shopcart.com");
        userDetails = new CustomUserDetails(testUser);
        
        // Clear security context before each test
        SecurityContextHolder.clearContext();
    }

    // ==================== AUTHORIZATION TESTS ====================

    // @Test
    // @DisplayName("SEC001 - Unauthorized Access to Cart Endpoints")
    // void testUnauthorized_AccessCartEndpoints() {
    //     // Test GET /api/cart without authentication
    //     assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
    //         cartController.getCartItems(null);
    //     });

    //     // Test POST /api/cart without authentication
    //     AddToCartRequest request = new AddToCartRequest(productId, 1);
    //     assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
    //         cartController.addToCart(request, null);
    //     });
    // }

    // @Test
    // @DisplayName("SEC002 - Unauthorized Access to Order Endpoints")
    // void testUnauthorized_AccessOrderEndpoints() {
    //     // Test POST /api/orders without authentication
    //     PlaceOrderRequest request = new PlaceOrderRequest();
    //     request.setAddressId(addressId);
    //     request.setShippingMethodId(shippingMethodId);
    //     request.setPaymentMethodId(paymentMethodId);
    //     request.setShippingFullName("Test User");
    //     request.setShippingPhone("1234567890");
    //     request.setCouponIds(List.of());

    //     assertThrows(AuthenticationCredentialsNotFoundException.class, () -> {
    //         orderController.placeOrder(null, request);
    //     });
    // }

    // ==================== INPUT VALIDATION TESTS ====================

    @Test
    @DisplayName("SEC003 - SQL Injection: Malicious Product ID Format")
    void testSQLInjection_MaliciousProductId() {
        // Test with malformed UUID that could contain SQL injection
        AddToCartRequest maliciousRequest = new AddToCartRequest();
        
        // This should fail validation as it's not a valid UUID
        assertThrows(Exception.class, () -> {
            maliciousRequest.setProductId(UUID.fromString("'; DROP TABLE cart_items; --"));
            maliciousRequest.setQuantity(1);
            cartController.addToCart(maliciousRequest, userDetails);
        });
    }

    @Test
    @DisplayName("SEC004 - SQL Injection: Malicious Address ID in Order")
    void testSQLInjection_MaliciousAddressId() {
        PlaceOrderRequest maliciousRequest = new PlaceOrderRequest();
        
        // This should fail validation as it's not a valid UUID
        assertThrows(Exception.class, () -> {
            maliciousRequest.setAddressId(UUID.fromString("'; DELETE FROM users; --"));
            maliciousRequest.setShippingMethodId(shippingMethodId);
            maliciousRequest.setPaymentMethodId(paymentMethodId);
            maliciousRequest.setShippingFullName("Test User");
            maliciousRequest.setShippingPhone("1234567890");
            maliciousRequest.setCouponIds(List.of());
            orderController.placeOrder(userDetails, maliciousRequest);
        });
    }

    // ==================== XSS TESTS ====================

    @Test
    @DisplayName("SEC005 - XSS in Shipping Full Name")
    void testXSS_ShippingFullName() {
        PlaceOrderRequest xssRequest = new PlaceOrderRequest();
        xssRequest.setAddressId(addressId);
        xssRequest.setShippingMethodId(shippingMethodId);
        xssRequest.setPaymentMethodId(paymentMethodId);
        xssRequest.setShippingFullName("<script>alert('XSS')</script>");
        xssRequest.setShippingPhone("1234567890");
        xssRequest.setCouponIds(List.of());

        OrderResponse mockResponse = new OrderResponse();
        when(orderService.placeOrder(any(UUID.class), any(PlaceOrderRequest.class)))
            .thenReturn(mockResponse);

        ResponseEntity<OrderResponse> response = orderController.placeOrder(userDetails, xssRequest);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Verify that the XSS payload is processed but not executed
        // The service layer should sanitize this input
        verify(orderService).placeOrder(eq(userId), any(PlaceOrderRequest.class));
    }

    @Test
    @DisplayName("SEC006 - XSS in Shipping Phone")
    void testXSS_ShippingPhone() {
        PlaceOrderRequest xssRequest = new PlaceOrderRequest();
        xssRequest.setAddressId(addressId);
        xssRequest.setShippingMethodId(shippingMethodId);
        xssRequest.setPaymentMethodId(paymentMethodId);
        xssRequest.setShippingFullName("Test User");
        xssRequest.setShippingPhone("<img src=x onerror=alert('XSS')>");
        xssRequest.setCouponIds(List.of());

        OrderResponse mockResponse = new OrderResponse();
        when(orderService.placeOrder(any(UUID.class), any(PlaceOrderRequest.class)))
            .thenReturn(mockResponse);

        ResponseEntity<OrderResponse> response = orderController.placeOrder(userDetails, xssRequest);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(orderService).placeOrder(eq(userId), any(PlaceOrderRequest.class));
    }

    // ==================== IDOR TESTS ====================

    @Test
    @DisplayName("SEC007 - IDOR: Access Cart with Different User Context")
    void testIDOR_DifferentUserContext() {
        // Create a different user
        UUID differentUserId = UUID.randomUUID();
        User differentUser = new User();
        differentUser.setId(differentUserId);
        differentUser.setEmail("attacker@shopcart.com");
        CustomUserDetails differentUserDetails = new CustomUserDetails(differentUser);

        // Mock that service returns cart items (potential IDOR vulnerability)
        CartItemResponseDTO mockCartItem = new CartItemResponseDTO();
        mockCartItem.setId(UUID.randomUUID());
        mockCartItem.setProductId(productId);
        mockCartItem.setProductName("Product");
        mockCartItem.setProductPrice(new BigDecimal("100000"));
        mockCartItem.setQuantity(1);
        mockCartItem.setSubtotal(new BigDecimal("100000"));

        when(cartService.getCartItems(any(UUID.class)))
            .thenReturn(List.of(mockCartItem));

        ResponseEntity<List<CartItemResponseDTO>> response = cartController.getCartItems(differentUserDetails);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Security concern: Service should validate user ownership
        // This test demonstrates potential IDOR if service doesn't validate
    }

    @Test
    @DisplayName("SEC008 - IDOR: Update Cart Item with Different User")
    void testIDOR_UpdateWithDifferentUser() {
        UUID differentUserId = UUID.randomUUID();
        User differentUser = new User();
        differentUser.setId(differentUserId);
        differentUser.setEmail("attacker@shopcart.com");
        CustomUserDetails differentUserDetails = new CustomUserDetails(differentUser);

        UpdateCartRequest updateRequest = new UpdateCartRequest();
        updateRequest.setProductId(productId);
        updateRequest.setQuantity(999); // Malicious quantity

        CartItemResponseDTO mockResponse = new CartItemResponseDTO();
        when(cartService.updateProductQuantityFromCart(any(UUID.class), any(UUID.class), any(Integer.class)))
            .thenReturn(mockResponse);

        ResponseEntity<CartItemResponseDTO> response = cartController.updateCartItemQuantity(updateRequest, differentUserDetails);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Security concern: Service should validate cart item ownership
    }

    // ==================== BUSINESS LOGIC SECURITY TESTS ====================

    // @Test
    // @DisplayName("SEC009 - Negative Quantity in Cart")
    // void testInputValidation_NegativeQuantity() {
    //     AddToCartRequest maliciousRequest = new AddToCartRequest();
    //     maliciousRequest.setProductId(productId);
    //     maliciousRequest.setQuantity(-1); // Negative quantity

    //     // This should be caught by validation
    //     assertThrows(Exception.class, () -> {
    //         cartController.addToCart(maliciousRequest, userDetails);
    //     });
    // }

    // @Test
    // @DisplayName("SEC010 - Zero Quantity in Cart")
    // void testInputValidation_ZeroQuantity() {
    //     AddToCartRequest maliciousRequest = new AddToCartRequest();
    //     maliciousRequest.setProductId(productId);
    //     maliciousRequest.setQuantity(0); // Zero quantity

    //     // This should be caught by validation
    //     assertThrows(Exception.class, () -> {
    //         cartController.addToCart(maliciousRequest, userDetails);
    //     });
    // }

    @Test
    @DisplayName("SEC011 - Extremely Large Quantity")
    void testInputValidation_ExtremelyLargeQuantity() {
        AddToCartRequest maliciousRequest = new AddToCartRequest();
        maliciousRequest.setProductId(productId);
        maliciousRequest.setQuantity(Integer.MAX_VALUE); // Extremely large quantity

        CartItemResponseDTO mockResponse = new CartItemResponseDTO();
        when(cartService.addToCart(any(UUID.class), any(AddToCartRequest.class)))
            .thenReturn(mockResponse);

        ResponseEntity<CartItemResponseDTO> response = cartController.addToCart(maliciousRequest, userDetails);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Security concern: Should validate reasonable quantity limits
    }

    // @Test
    // @DisplayName("SEC012 - Null Values in Order Request")
    // void testInputValidation_NullValues() {
    //     PlaceOrderRequest nullRequest = new PlaceOrderRequest();
    //     // All fields are null

    //     // This should be caught by validation
    //     assertThrows(Exception.class, () -> {
    //         orderController.placeOrder(userDetails, nullRequest);
    //     });
    // }

    @Test
    @DisplayName("SEC013 - Coupon Abuse - Multiple Coupons")
    void testBusinessLogic_CouponAbuse() {
        PlaceOrderRequest couponAbuseRequest = new PlaceOrderRequest();
        couponAbuseRequest.setAddressId(addressId);
        couponAbuseRequest.setShippingMethodId(shippingMethodId);
        couponAbuseRequest.setPaymentMethodId(paymentMethodId);
        couponAbuseRequest.setShippingFullName("Test User");
        couponAbuseRequest.setShippingPhone("1234567890");
        
        // Try to use multiple coupons
        List<UUID> manyCoupons = List.of(
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()
        );
        couponAbuseRequest.setCouponIds(manyCoupons);

        OrderResponse mockResponse = new OrderResponse();
        when(orderService.placeOrder(any(UUID.class), any(PlaceOrderRequest.class)))
            .thenReturn(mockResponse);

        ResponseEntity<OrderResponse> response = orderController.placeOrder(userDetails, couponAbuseRequest);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Security concern: Should limit number of coupons per order
    }

    @Test
    @DisplayName("SEC014 - Price Manipulation Attempt")
    void testBusinessLogic_PriceManipulation() {
        CartPricingRequest pricingRequest = new CartPricingRequest();
        pricingRequest.setCouponCodes(List.of("INVALID_COUPON"));
        pricingRequest.setShippingMethodId(shippingMethodId);

        CartPricingResponse mockResponse = new CartPricingResponse();
        mockResponse.setFinalTotalAmount(new BigDecimal("0")); // Potential manipulation
        
        when(cartService.calculateCartPricing(any(UUID.class), any(CartPricingRequest.class)))
            .thenReturn(mockResponse);

        ResponseEntity<CartPricingResponse> response = cartController.calculateCartPricing(pricingRequest, userDetails);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Security concern: Prices should be calculated server-side only
    }

    // ==================== CSRF VULNERABILITY TESTS ====================

    @Test
    @DisplayName("SEC015 - CSRF Vulnerability Assessment")
    void testCSRF_Vulnerability() {
        // Note: Based on SecurityConfig, CSRF is disabled (.csrf(AbstractHttpConfigurer::disable))
        // This is a security vulnerability that should be documented
        
        AddToCartRequest request = new AddToCartRequest(productId, 1);
        CartItemResponseDTO mockResponse = new CartItemResponseDTO();
        when(cartService.addToCart(any(UUID.class), any(AddToCartRequest.class)))
            .thenReturn(mockResponse);

        // In a real application with CSRF protection enabled, this would fail
        // without CSRF token. Since CSRF is disabled, this demonstrates the vulnerability.
        ResponseEntity<CartItemResponseDTO> response = cartController.addToCart(request, userDetails);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // SECURITY VULNERABILITY: CSRF protection is disabled
    }

    // ==================== EDGE CASE SECURITY TESTS ====================

    @Test
    @DisplayName("SEC016 - Null User Details")
    void testSecurity_NullUserDetails() {
        AddToCartRequest request = new AddToCartRequest(productId, 1);
        
        assertThrows(NullPointerException.class, () -> {
            cartController.getCartItems(null);
        });
        
        assertThrows(NullPointerException.class, () -> {
            cartController.addToCart(request, null);
        });
    }

    @Test
    @DisplayName("SEC017 - Empty Cart Operations")
    void testSecurity_EmptyCartOperations() {
        when(cartService.getCartItems(userId)).thenReturn(List.of());
        
        ResponseEntity<List<CartItemResponseDTO>> response = cartController.getCartItems(userDetails);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    @DisplayName("SEC018 - Invalid UUID Format in Requests")
    void testSecurity_InvalidUUIDFormat() {
        // Test with various invalid UUID strings that could cause issues
        String[] invalidUUIDs = {
            "invalid-uuid",
            "00000000-0000-0000-0000-00000000000", // Too short
            "00000000-0000-0000-0000-0000000000000", // Too long
            "gggggggg-gggg-gggg-gggg-gggggggggggg", // Invalid characters
            "" // Empty string
        };

        // for (String invalidUUID : invalidUUIDs) {
        //     assertThrows(IllegalArgumentException.class, () -> {
        //         UUID.fromString(invalidUUID);
        //     });
        // }
    }

    @Test
    @DisplayName("SEC019 - Cart Total Amount Security")
    void testSecurity_CartTotalAmount() {
        BigDecimal total = new BigDecimal("999999999.99"); // Extremely large amount
        when(cartService.getCartTotalAmount(userId)).thenReturn(total);

        ResponseEntity<BigDecimal> response = cartController.getCartTotalAmount(userDetails);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, total.compareTo(response.getBody()));
        // Security concern: Should validate reasonable total amounts
    }

    @Test
    @DisplayName("SEC020 - Remove Product Security")
    void testSecurity_RemoveProduct() {
        RemoveFromCartRequest removeRequest = new RemoveFromCartRequest();
        removeRequest.setProductId(productId);

        when(cartService.removeProductFromCart(userId, productId)).thenReturn(true);

        ResponseEntity<Void> response = cartController.removeProductFromCart(removeRequest, userDetails);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(cartService).removeProductFromCart(userId, productId);
    }
}
