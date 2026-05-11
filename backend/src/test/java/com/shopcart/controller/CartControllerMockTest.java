package com.shopcart.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.springframework.http.ResponseEntity;

import com.shopcart.dto.AddToCartRequest;
import com.shopcart.dto.CartItemResponseDTO;
import com.shopcart.dto.CartPricingRequest;
import com.shopcart.dto.CartPricingResponse;
import com.shopcart.dto.RemoveFromCartRequest;
import com.shopcart.dto.UpdateCartRequest;
import com.shopcart.security.CustomUserDetails;
import com.shopcart.service.CartService;

@DisplayName("Cart Controller Mock Tests")
@ExtendWith(MockitoExtension.class)
class CartControllerMockTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private UUID userId;
    private UUID productId;
    private CustomUserDetails userDetails;
    private CartItemResponseDTO cartItemResponse;

    @BeforeEach
    public void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();

        // Giả lập User
        com.shopcart.entity.User user = new com.shopcart.entity.User();
        user.setId(userId);
        user.setEmail("phuc@shopcart.com");
        userDetails = new CustomUserDetails(user);

        // Giả lập Response DTO
        cartItemResponse = new CartItemResponseDTO();
        cartItemResponse.setId(UUID.randomUUID());
        cartItemResponse.setProductId(productId);
        cartItemResponse.setProductName("Sản phẩm mẫu");
        cartItemResponse.setProductPrice(new BigDecimal("150000"));
        cartItemResponse.setQuantity(2);
        cartItemResponse.setSubtotal(new BigDecimal("300000"));
    }

    @Test
    @DisplayName("TC01 - Get Cart Items Success")
    void testGetCartItems_Success() {
        when(cartService.getCartItems(userId)).thenReturn(List.of(cartItemResponse));

        ResponseEntity<List<CartItemResponseDTO>> response = cartController.getCartItems(userDetails);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        verify(cartService).getCartItems(userId);
    }

    @Test
    @DisplayName("TC02 - Add To Cart Success")
    void testAddToCart_Success() {
        AddToCartRequest request = new AddToCartRequest(productId, 2);
        when(cartService.addToCart(eq(userId), any(AddToCartRequest.class))).thenReturn(cartItemResponse);

        ResponseEntity<CartItemResponseDTO> response = cartController.addToCart(request, userDetails);

        assertNotNull(response.getBody());
        assertEquals(200, response.getStatusCode().value());
        verify(cartService).addToCart(eq(userId), any(AddToCartRequest.class));
    }

    @Test
    @DisplayName("TC03 - Update Quantity Success")
    void testUpdateCartItemQuantity_Success() {
        // Khớp với UpdateCartRequest trong Controller
        UpdateCartRequest updateRequest = new UpdateCartRequest();
        updateRequest.setProductId(productId);
        updateRequest.setQuantity(5);

        when(cartService.updateProductQuantityFromCart(userId, productId, 5)).thenReturn(cartItemResponse);

        ResponseEntity<CartItemResponseDTO> response = cartController.updateCartItemQuantity(updateRequest, userDetails);

        assertEquals(200, response.getStatusCode().value());
        verify(cartService).updateProductQuantityFromCart(userId, productId, 5);
    }

    @Test
    @DisplayName("TC04 - Remove Product Success")
    void testRemoveProductFromCart_Success() {
        // Khớp với RemoveFromCartRequest trong Controller
        RemoveFromCartRequest removeRequest = new RemoveFromCartRequest();
        removeRequest.setProductId(productId);

        when(cartService.removeProductFromCart(userId, productId)).thenReturn(true);

        ResponseEntity<Void> response = cartController.removeProductFromCart(removeRequest, userDetails);

        assertEquals(200, response.getStatusCode().value());
    }

    @Test
    @DisplayName("TC05 - Remove Product Not Found (404)")
    void testRemoveProductFromCart_NotFound() {
        RemoveFromCartRequest removeRequest = new RemoveFromCartRequest();
        removeRequest.setProductId(productId);

        when(cartService.removeProductFromCart(userId, productId)).thenReturn(false);

        ResponseEntity<Void> response = cartController.removeProductFromCart(removeRequest, userDetails);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    @DisplayName("TC06 - Get Total Amount")
    void testGetCartTotalAmount() {
        BigDecimal total = new BigDecimal("1000000");
        when(cartService.getCartTotalAmount(userId)).thenReturn(total);

        ResponseEntity<BigDecimal> response = cartController.getCartTotalAmount(userDetails);

        assertEquals(0, total.compareTo(response.getBody()));
    }

    @Test
    @DisplayName("TC07 - Calculate Pricing Success")
    void testCalculateCartPricing_Success() {
        CartPricingRequest pRequest = new CartPricingRequest(List.of("KM10"), UUID.randomUUID());
        CartPricingResponse pResponse = new CartPricingResponse();
        pResponse.setFinalTotalAmount(new BigDecimal("900000"));

        when(cartService.calculateCartPricing(eq(userId), any(CartPricingRequest.class))).thenReturn(pResponse);

        ResponseEntity<CartPricingResponse> response = cartController.calculateCartPricing(pRequest, userDetails);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("TC08 - Handle Null User (Edge Case)")
    void testGetCartItems_NullUser() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> cartController.getCartItems(null));
        assertNotNull(exception);
    }
}