package com.shopcart;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.shopcart.dto.AddToCartRequest;
import com.shopcart.dto.CartItemResponseDTO;
import com.shopcart.dto.UpdateCartRequest;
import com.shopcart.entity.CartItem;
import com.shopcart.entity.Inventory;
import com.shopcart.entity.Product;
import com.shopcart.entity.User;
import com.shopcart.repository.CartItemRepository;
import com.shopcart.repository.InventoryRepository;
import com.shopcart.repository.ProductImageRepository;
import com.shopcart.repository.ProductRepository;
import com.shopcart.repository.UserRepository;
import com.shopcart.service.CartServiceImpl;

@DisplayName("Cart Service Unit Tests")
@ExtendWith(MockitoExtension.class)
class CartTest {

    @Mock private CartItemRepository cartItemRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProductRepository productRepository;
    @Mock private InventoryRepository inventoryRepository;
    @Mock private ProductImageRepository productImageRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private UUID userId;
    private UUID productId;
    private User mockUser;
    private Product mockProduct;
    private Inventory mockInventory;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        productId = UUID.randomUUID();

        mockUser = new User();
        mockUser.setId(userId);

        mockProduct = new Product();
        mockProduct.setId(productId);
        mockProduct.setName("Vot cau long Yonex");
        mockProduct.setPrice(new BigDecimal("15000000"));
        mockProduct.setStatus("ACTIVE");

        mockInventory = new Inventory();
        mockInventory.setQuantity(10);
    }

    // --- a) Test Scenarios cho addToCart() ---

    @Test
    @DisplayName("TC1: Thêm sản phẩm mới vào giỏ hàng thành công")
    void testAddToCart_Success() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(inventoryRepository.findByProductId(productId)).thenReturn(mockInventory);
        when(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(null);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);

        CartItemResponseDTO result = cartService.addToCart(userId, request);

        assertNotNull(result);
        assertEquals(2, result.getQuantity());
        assertEquals("Vot cau long Yonex", result.getProductName());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    @DisplayName("TC2: Thêm sản phẩm đã có trong giỏ (cộng dồn số lượng)")
    void testAddToCart_UpdateExisting() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(3);

        CartItem existingItem = new CartItem(mockUser, mockProduct, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(inventoryRepository.findByProductId(productId)).thenReturn(mockInventory);
        when(cartItemRepository.findByUserIdAndProductId(userId, productId)).thenReturn(existingItem);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);

        CartItemResponseDTO result = cartService.addToCart(userId, request);

        assertEquals(5, result.getQuantity()); // 2 cũ + 3 mới
        verify(cartItemRepository, times(1)).save(existingItem);
    }

    @Test
    @DisplayName("TC3: Thêm khi tồn kho không đủ")
    void testAddToCart_InsufficientInventory() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);
        request.setQuantity(11); // Tồn kho chỉ có 10

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(productId)).thenReturn(Optional.of(mockProduct));
        when(inventoryRepository.findByProductId(productId)).thenReturn(mockInventory);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(userId, request);
        });

        assertTrue(exception.getMessage().contains("Insufficient inventory"));
    }

    @Test
    @DisplayName("TC4: Thêm sản phẩm không tồn tại")
    void testAddToCart_ProductNotFound() {
        AddToCartRequest request = new AddToCartRequest();
        request.setProductId(productId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            cartService.addToCart(userId, request);
        });
    }

    // --- b) Test cho removeFromCart() và updateQuantity() ---

    @Test
    @DisplayName("TC5: Cập nhật số lượng sản phẩm thành công")
    void testUpdateQuantity_Success() {
        UUID cartItemId = UUID.randomUUID();
        UpdateCartRequest request = new UpdateCartRequest();
        request.setQuantity(5);

        CartItem existingItem = new CartItem(mockUser, mockProduct, 1);
        existingItem.setId(cartItemId);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(existingItem));
        when(inventoryRepository.findByProductId(productId)).thenReturn(mockInventory);
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArguments()[0]);

        CartItemResponseDTO result = cartService.updateCartItemQuantity(userId, cartItemId, request);

        assertEquals(5, result.getQuantity());
        verify(cartItemRepository).save(existingItem);
    }

    @Test
    @DisplayName("TC6: Xóa sản phẩm khỏi giỏ hàng")
    void testRemoveFromCart() {
        UUID cartItemId = UUID.randomUUID();
        CartItem existingItem = new CartItem(mockUser, mockProduct, 1);
        existingItem.setId(cartItemId);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(existingItem));

        boolean removed = cartService.removeFromCart(userId, cartItemId);

        assertTrue(removed);
        verify(cartItemRepository, times(1)).delete(existingItem);
    }

    @Test
    @DisplayName("TC7: Xóa sản phẩm không thuộc về user (Security check)")
    void testRemoveFromCart_NotOwner() {
        UUID cartItemId = UUID.randomUUID();
        User differentUser = new User();
        differentUser.setId(UUID.randomUUID()); // ID khác với userId hiện tại

        CartItem existingItem = new CartItem(differentUser, mockProduct, 1);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(existingItem));

        boolean removed = cartService.removeFromCart(userId, cartItemId);

        assertFalse(removed);
        verify(cartItemRepository, never()).delete(any());
    }
}