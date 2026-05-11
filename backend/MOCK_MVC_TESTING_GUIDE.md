# Hướng Dẫn Viết MockMvc Test cho Spring Boot

## Tổng Quan

MockMvc là một framework testing mạnh mẽ của Spring Boot giúp kiểm thử các REST Controller mà không cần khởi động toàn bộ ứng dụng. Document này hướng dẫn cách viết MockMvc test cho OrderController dựa trên ví dụ thực tế đã triển khai.

## Kiến Trúc Test

### 1. Cấu Trúc File Test

```java
@DisplayName("Order Controller Unit Tests")
@ExtendWith(MockitoExtension.class)
class OrderControllerUnitTest {
    
    @Mock
    private OrderService orderService;
    
    @InjectMocks
    private OrderController orderController;
    
    // Test data setup
    private CustomUserDetails mockUserDetails;
    private UUID testUserId;
    private PlaceOrderRequest validRequest;
    private OrderResponse mockResponse;
}
```

### 2. Các Annotation Quan Trọng

- **@ExtendWith(MockitoExtension.class)**: Kích hoạt Mockito framework
- **@Mock**: Tạo mock object cho dependencies
- **@InjectMocks**: Tạo instance của controller và inject mocks vào
- **@DisplayName**: Đặt tên descriptive cho test method
- **@BeforeEach**: Setup data trước mỗi test case

## 3. Quy Trình Viết Test

### Bước 1: Setup Test Data

```java
@BeforeEach
void setUp() {
    // Tạo user ID
    testUserId = UUID.randomUUID();
    
    // Tạo mock user cho authentication
    User mockUser = new User();
    mockUser.setId(testUserId);
    mockUser.setEmail("test@example.com");
    mockUser.setHashPassword("hashedPassword");
    
    mockUserDetails = new CustomUserDetails(mockUser);
    
    // Tạo request data
    validRequest = new PlaceOrderRequest(
        UUID.randomUUID(), // addressId
        UUID.randomUUID(), // shippingMethodId
        UUID.randomUUID(), // paymentMethodId
        "John Doe",       // shippingFullName
        "0123456789",     // shippingPhone
        List.of(UUID.randomUUID()) // couponIds
    );
}
```

### Bước 2: Mock Service Behavior

```java
// Mock service để trả về dữ liệu giả
when(orderService.placeOrder(eq(testUserId), any(PlaceOrderRequest.class)))
    .thenReturn(mockResponse);

// Mock service để ném exception
when(orderService.placeOrder(eq(testUserId), any(PlaceOrderRequest.class)))
    .thenThrow(new IllegalArgumentException("Invalid address ID"));
```

### Bước 3: Execute Controller Method

```java
// Gọi controller method
ResponseEntity<OrderResponse> response = orderController.placeOrder(mockUserDetails, validRequest);
```

### Bước 4: Verify Results

```java
// Kiểm tra HTTP status
assertEquals(200, response.getStatusCode().value());

// Kiểm tra response body
assertNotNull(response.getBody());
assertEquals(mockResponse.getId(), response.getBody().getId());
assertEquals("PENDING", response.getBody().getStatus());

// Kiểm tra nested objects
assertEquals("John Doe", response.getBody().getShippingInfo().getFullName());
assertEquals(new BigDecimal("25000000"), response.getBody().getItems().get(0).getPrice());

// Verify service được gọi với đúng parameters
verify(orderService, times(1)).placeOrder(testUserId, validRequest);
```

## 4. Các Pattern Testing Thông Dụ

### Pattern 1: Happy Path Test

```java
@Test
@DisplayName("TC1: Place Order - Success case")
void testPlaceOrder_Success() {
    // Arrange
    when(orderService.placeOrder(eq(testUserId), any(PlaceOrderRequest.class)))
        .thenReturn(mockResponse);
    
    // Act
    ResponseEntity<OrderResponse> response = orderController.placeOrder(mockUserDetails, validRequest);
    
    // Assert
    assertNotNull(response);
    assertEquals(200, response.getStatusCode().value());
    // ... additional assertions
}
```

### Pattern 2: Exception Handling Test

```java
@Test
@DisplayName("TC2: Place Order - Service throws IllegalArgumentException")
void testPlaceOrder_ServiceThrowsIllegalArgumentException() {
    // Arrange
    when(orderService.placeOrder(eq(testUserId), any(PlaceOrderRequest.class)))
        .thenThrow(new IllegalArgumentException("Invalid address ID"));
    
    // Act & Assert
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
        orderController.placeOrder(mockUserDetails, validRequest);
    });
    
    assertEquals("Invalid address ID", exception.getMessage());
}
```

### Pattern 3: Edge Cases Test

```java
@Test
@DisplayName("TC3: Place Order - Order with no coupons")
void testPlaceOrder_NoCoupons() {
    // Arrange - Create request with no coupons
    PlaceOrderRequest requestNoCoupons = new PlaceOrderRequest(
        UUID.randomUUID(),
        UUID.randomUUID(),
        UUID.randomUUID(),
        "Jane Smith",
        "0987654321",
        List.of() // Empty coupon list
    );
    
    // ... rest of test
}
```

## 5. Mockito Methods Thông Dụng

### Stubbing Methods

```java
// Trả về giá trị cụ thể
when(service.method()).thenReturn(value);

// Ném exception
when(service.method()).thenThrow(new RuntimeException());

// Trả về giá trị khác nhau cho các lần gọi khác nhau
when(service.method())
    .thenReturn(value1)
    .thenReturn(value2);

// Sử dụng any() cho bất kỳ parameter nào
when(service.method(any(PlaceOrderRequest.class))).thenReturn(value);

// Sử dụng eq() cho parameter cụ thể
when(service.method(eq(testUserId), any())).thenReturn(value);
```

### Verification Methods

```java
// Verify method được gọi 1 lần
verify(service, times(1)).method();

// Verify method không được gọi
verify(service, never()).method();

// Verify method được gọi ít nhất 1 lần
verify(service, atLeastOnce()).method();

// Verify với parameters cụ thể
verify(service).method(eq(testUserId), any());
```

## 6. Best Practices

### ✅ Nên Làm

1. **Descriptive Test Names**: Sử dụng @DisplayName với tên rõ ràng
2. **Arrange-Act-Assert**: Tổ chức test theo 3 phần rõ ràng
3. **Test One Thing**: Mỗi test chỉ kiểm tra một scenario
4. **Use Builders**: Sử dụng Builder pattern cho complex objects
5. **Verify Mocks**: Luôn verify mock interactions
6. **Edge Cases**: Test cả normal, error, và edge cases

### ❌ Không Nên Làm

1. **Unnecessary Stubbing**: Không mock method không được sử dụng
2. **Test Implementation**: Không test internal implementation
3. **Hardcoded Values**: Tránh hardcode test data
4. **Complex Tests**: Giữ test đơn giản và dễ hiểu
5. **Missing Assertions**: Luôn có assertions cho kết quả

## 7. Test Data Management

### Sử dụng @BeforeEach cho Shared Data

```java
@BeforeEach
void setUp() {
    // Setup data được sử dụng bởi nhiều test cases
    testUserId = UUID.randomUUID();
    validRequest = createValidRequest();
    mockResponse = createMockResponse();
}
```

### Sử dụng Builder Pattern cho Complex Objects

```java
OrderResponse mockResponse = OrderResponse.builder()
    .id(UUID.randomUUID())
    .status("PENDING")
    .shippingInfo(OrderResponse.ShippingInfo.builder()
        .fullName("John Doe")
        .phone("0123456789")
        .build())
    .build();
```

## 8. Integration với Maven

### Dependencies Cần Thiết

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-core</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
```

### Run Tests

```bash
# Run tất cả tests
mvn test

# Run specific test class
mvn test -Dtest=OrderControllerUnitTest

# Run specific test method
mvn test -Dtest=OrderControllerUnitTest#testPlaceOrder_Success
```

## 9. Ví dụ Test Cases cho OrderController

### Test Case 1: Happy Path
- **Scenario**: User đặt hàng thành công
- **Setup**: Mock service trả về OrderResponse
- **Verify**: HTTP 200, response data đúng, service được gọi 1 lần

### Test Case 2: Validation Error
- **Scenario**: Service ném IllegalArgumentException
- **Setup**: Mock service ném exception
- **Verify**: Exception được throw với message đúng

### Test Case 3: Business Logic Error
- **Scenario**: Service ném IllegalStateException
- **Setup**: Mock service ném exception
- **Verify**: Exception được throw với message đúng

### Test Case 4: No Coupons
- **Scenario**: Đặt hàng không sử dụng coupon
- **Setup**: Request với empty coupon list
- **Verify**: Response không có appliedCoupons

### Test Case 5: Multiple Items
- **Scenario**: Đặt hàng với nhiều sản phẩm
- **Setup**: Mock response với multiple items
- **Verify**: Response chứa đúng số lượng items

## 10. Troubleshooting

### Common Issues

1. **UnnecessaryStubbingException**: Remove unused when() statements
2. **NullPointerException**: Check null parameters in test setup
3. **MockitoException**: Ensure correct mock setup and verification
4. **Dependency Issues**: Verify all test dependencies in pom.xml

### Debug Tips

```java
// Enable lenient stubbing for complex tests
@Mock(lenient = true)
private OrderService orderService;

// Print actual vs expected values
System.out.println("Expected: " + expectedValue);
System.out.println("Actual: " + actualValue);
```

## 11. Kết Luận

MockMvc testing là kỹ thuật quan trọng để đảm bảo chất lượng REST API. Bằng cách sử dụng các patterns và best practices trong guide này, bạn có thể viết tests hiệu quả, maintainable, và comprehensive cho các Spring Boot controllers.

**Key Takeaways**:
- Sử dụng MockitoExtension cho unit testing
- Mock dependencies với @Mock và @InjectMocks
- Follow Arrange-Act-Assert pattern
- Test cả success và failure scenarios
- Verify mock interactions
- Keep tests simple and focused

---

*Document này được tạo dựa trên ví dụ thực tế từ OrderController trong project ShopCart.*
