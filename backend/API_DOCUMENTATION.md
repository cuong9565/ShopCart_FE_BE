# ShopCart Backend API Documentation

## Table of Contents
1. [Authentication APIs](#authentication-apis)
2. [Category APIs](#category-apis)
3. [Product APIs](#product-apis)
4. [Cart APIs](#cart-apis)
5. [Address APIs](#address-apis)
6. [Shipping Methods APIs](#shipping-methods-apis)
7. [Payment Methods APIs](#payment-methods-apis)

---

## Authentication APIs

### Overview
Backend sử dụng **Session-based Authentication** với Spring Boot Security. Session được tự động quản lý và gửi qua HTTP cookies.

---

## 1. Login API

### Endpoint
`POST /api/auth/login`

### Mục đích
Xác thực người dùng và tạo session

### Endpoint Demo
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@example.com",
    "password": "password123"
  }'
```

### Validation Rules
- `email`: Bắt buộc, phải là email hợp lệ
- `password`: Bắt buộc, tối thiểu 6 ký tự

### Success Response (200 OK)
```json
{
  "email": "user@example.com",
  "role": "ROLE_USER",
  "userId": "123e4567-e89b-12d3-a456-426614174000"
}
```

### Error Response (401 Unauthorized)
```json
{
  "email": null,
  "role": null,
  "userId": null
}
```

---

## 2. Check Authentication Status API

### Endpoint
`GET /api/auth/check`

### Mục đích
Kiểm tra trạng thái authentication hiện tại

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/auth/check \
  -H "Cookie: JSESSIONID=ABC123..."
```

### Success Response (200 OK)
```json
{
  "email": "user@example.com",
  "role": "ROLE_USER",
  "userId": "123e4567-e89b-12d3-a456-426614174000"
}
```

### Error Response (401 Unauthorized)
```json
"Not authenticated"
```

---

## 3. Logout API

### Endpoint
`POST /api/auth/logout`

### Mục đích
Đăng xuất và invalidate session

### Endpoint Demo
```bash
curl -X POST http://localhost:8080/api/auth/logout \
  -H "Cookie: JSESSIONID=ABC123..."
```

---

## Category APIs

### Overview
Quản lý danh mục sản phẩm với các thao tác CRUD cơ bản.

---

## 1. Get All Categories

### Endpoint
`GET /api/categories`

### Mục đích
Lấy danh sách tất cả danh mục

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/categories
```

### Success Response (200 OK)
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "Electronics",
    "description": "Electronic devices and accessories",
    "createdAt": "2026-05-09T10:30:00"
  }
]
```

---

## Product APIs

### Overview
Quản lý sản phẩm với các tính năng tìm kiếm, lọc theo danh mục, và sản phẩm nổi bật.

---

## 1. Get All Products

### Endpoint
`GET /api/products`

### Mục đích
Lấy danh sách tất cả sản phẩm (không có description)

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/products
```

### Success Response (200 OK)
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "iPhone 15 Pro",
    "price": 999.99,
    "status": "ACTIVE",
    "slug": "iphone-15-pro",
    "createdAt": "2026-05-09T10:30:00",
    "category": {
      "id": "456e7890-f12c-34d5-b789-012345678901",
      "name": "Electronics",
      "createdAt": "2026-05-09T09:00:00"
    },
    "stockQuantity": 50,
    "thumbnailImage": "https://example.com/iphone-thumb.jpg"
  }
]
```

---

## 2. Get Featured Products

### Endpoint
`GET /api/products/featured`

### Mục đích
Lấy danh sách sản phẩm nổi bật (không có description)

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/products/featured
```

### Success Response (200 OK)
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "name": "iPhone 15 Pro",
    "price": 999.99,
    "status": "ACTIVE",
    "slug": "iphone-15-pro",
    "createdAt": "2026-05-09T10:30:00",
    "category": {
      "id": "456e7890-f12c-34d5-b789-012345678901",
      "name": "Electronics",
      "createdAt": "2026-05-09T09:00:00"
    },
    "stockQuantity": 50,
    "thumbnailImage": "https://example.com/iphone-thumb.jpg"
  }
]
```

---

## 3. Get Product detail by ID

### Endpoint
`GET /api/products/detail/{id}`

### Mục đích
Lấy thông tin chi tiết sản phẩm theo ID (có description, images)

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/products/detail/4569823e-8674-477d-9f02-ce0ddb60dcb1
```

### Success Response (200 OK)
```json
{
    "id": "4569823e-8674-477d-9f02-ce0ddb60dcb1",
    "name": "Vợt cầu lông Lining Bladex Assassin",
    "price": 1300000.00,
    "description": "Vợt cầu lông Lining Bladex Assassin là lựa chọn nổi bật trong phân khúc tầm trung năm 2026, hướng đến lối chơi tốc độ cao và phản xạ nhanh. Ngay từ những pha cầu đầu tiên, cây vợt mang lại cảm giác vung gọn, thoát tay và cực kỳ linh hoạt, giúp người chơi dễ dàng kiểm soát nhịp độ trận đấu.\nNhờ thiết kế khung tối ưu khí động học kết hợp cùng trục vợt siêu mỏng 6.6mm, Bladex Assassin cho khả năng tăng tốc đầu vợt nhanh, hỗ trợ hiệu quả trong các tình huống đôi công, phản tạt liên tục. Đặc biệt, độ đàn hồi tốt giúp các cú đánh có độ “bật” rõ rệt, mang lại phản hồi nhanh và chính xác trong từng pha xử lý.\nKhông chỉ mạnh ở tốc độ, cây vợt còn duy trì khả năng tấn công ổn định nhờ điểm cân bằng hơi nặng đầu khoảng 305mm. Điều này giúp người chơi vừa có thể đập cầu dứt khoát từ cuối sân, vừa linh hoạt xử lý nhanh ở khu vực lưới. Tổng thể, đây là mẫu vợt cân bằng giữa tốc độ, kiểm soát và sức mạnh, rất phù hợp cho lối đánh hiện đại, đặc biệt trong đánh đôi.",
    "status": "ACTIVE",
    "slug": "vot-cau-long-lining-bladex-assassin",
    "createdAt": "2026-05-08T12:08:01",
    "category": {
        "id": "33960144-4307-425a-8c95-11fb62f4c438",
        "name": "Vợt cầu lông Lining",
        "createdAt": "2026-05-08T08:23:13.671038"
    },
    "stockQuantity": 100,
    "images": [
        "https://htfpdyvdrsjztkuopkmf.supabase.co/storage/v1/object/public/product-images/products/4569823e-8674-477d-9f02-ce0ddb60dcb1/1.webp",
        "https://htfpdyvdrsjztkuopkmf.supabase.co/storage/v1/object/public/product-images/products/4569823e-8674-477d-9f02-ce0ddb60dcb1/2.webp",
        "https://htfpdyvdrsjztkuopkmf.supabase.co/storage/v1/object/public/product-images/products/4569823e-8674-477d-9f02-ce0ddb60dcb1/3.webp",
        "https://htfpdyvdrsjztkuopkmf.supabase.co/storage/v1/object/public/product-images/products/4569823e-8674-477d-9f02-ce0ddb60dcb1/4.webp",
        "https://htfpdyvdrsjztkuopkmf.supabase.co/storage/v1/object/public/product-images/products/4569823e-8674-477d-9f02-ce0ddb60dcb1/5.webp",
        "https://htfpdyvdrsjztkuopkmf.supabase.co/storage/v1/object/public/product-images/products/4569823e-8674-477d-9f02-ce0ddb60dcb1/6.webp",
        "https://htfpdyvdrsjztkuopkmf.supabase.co/storage/v1/object/public/product-images/products/4569823e-8674-477d-9f02-ce0ddb60dcb1/7.webp"
    ]
}
```

---

## Cart APIs

### Overview
Quản lý giỏ hàng người dùng với các thao tác thêm, cập nhật, và xóa sản phẩm. Yêu cầu authentication để truy cập.

---

## 1. Get Cart Items

### Endpoint
`GET /api/cart`

### Mục đích
Lấy danh sách tất cả sản phẩm trong giỏ hàng của người dùng đã đăng nhập

### Authentication
Yêu cầu session hợp lệ (đã đăng nhập)

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/cart \
  -H "Cookie: JSESSIONID=ABC123..."
```

### Success Response (200 OK)
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "productId": "456e7890-f12c-34d5-b789-012345678901",
    "productName": "iPhone 15 Pro",
    "productPrice": 999.99,
    "thumbnailImage": "https://example.com/iphone-thumb.jpg",
    "quantity": 2,
    "subtotal": 1999.98,
    "productStatus": "ACTIVE",
    "productSlug": "iphone-15-pro",
    "createdAt": "2026-05-09T10:30:00"
  }
]
```

---

## 2. Add Product to Cart

### Endpoint
`POST /api/cart`

### Mục đích
Thêm sản phẩm vào giỏ hàng. Nếu sản phẩm đã tồn tại, số lượng sẽ được tăng lên.

### Authentication
Yêu cầu session hợp lệ (đã đăng nhập)

### Request Body
```json
{
  "productId": "456e7890-f12c-34d5-b789-012345678901",
  "quantity": 1
}
```

### Validation Rules
- `productId`: Bắt buộc, phải là UUID hợp lệ
- `quantity`: Bắt buộc, phải >= 1

### Endpoint Demo
```bash
curl -X POST http://localhost:8080/api/cart \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=ABC123..." \
  -d '{
    "productId": "456e7890-f12c-34d5-b789-012345678901",
    "quantity": 1
  }'
```

### Success Response (200 OK)
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "productId": "456e7890-f12c-34d5-b789-012345678901",
  "productName": "iPhone 15 Pro",
  "productPrice": 999.99,
  "thumbnailImage": "https://example.com/iphone-thumb.jpg",
  "quantity": 1,
  "subtotal": 999.99,
  "productStatus": "ACTIVE",
  "productSlug": "iphone-15-pro",
  "createdAt": "2026-05-09T10:30:00"
}
```

---

## 3. Update Cart Item Quantity

### Endpoint
`PUT /api/cart`

### Mục đích
Cập nhật số lượng của sản phẩm trong giỏ hàng

### Authentication
Yêu cầu session hợp lệ (đã đăng nhập)

### Request Body
```json
{
  "productId": "456e7890-f12c-34d5-b789-012345678901",
  "quantity": 3
}
```

### Validation Rules
- `productId`: Bắt buộc, phải là UUID hợp lệ
- `quantity`: Bắt buộc, phải >= 1

### Endpoint Demo
```bash
curl -X PUT http://localhost:8080/api/cart \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=ABC123..." \
  -d '{
    "productId": "456e7890-f12c-34d5-b789-012345678901",
    "quantity": 3
  }'
```

### Success Response (200 OK)
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "productId": "456e7890-f12c-34d5-b789-012345678901",
  "productName": "iPhone 15 Pro",
  "productPrice": 999.99,
  "thumbnailImage": "https://example.com/iphone-thumb.jpg",
  "quantity": 3,
  "subtotal": 2999.97,
  "productStatus": "ACTIVE",
  "productSlug": "iphone-15-pro",
  "createdAt": "2026-05-09T10:30:00"
}
```

---

## 4. Remove Product from Cart

### Endpoint
`DELETE /api/cart`

### Mục đích
Xóa sản phẩm khỏi giỏ hàng

### Authentication
Yêu cầu session hợp lệ (đã đăng nhập)

### Request Body
```json
{
  "productId": "456e7890-f12c-34d5-b789-012345678901"
}
```

### Validation Rules
- `productId`: Bắt buộc, phải là UUID hợp lệ

### Endpoint Demo
```bash
curl -X DELETE http://localhost:8080/api/cart \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=ABC123..." \
  -d '{
    "productId": "456e7890-f12c-34d5-b789-012345678901"
  }'
```

### Success Response (200 OK)
```json
{
  "message": "Product removed from cart successfully"
}
```

### Error Response (404 Not Found)
```json
{
  "error": "Product not found in cart",
  "status": "NOT_FOUND"
}
```

---

## Address APIs

### Overview
Quản lý địa chỉ giao hàng của người dùng với các thao tác lấy danh sách và thêm địa chỉ. Yêu cầu authentication để truy cập.

---

## 1. Get User Addresses

### Endpoint
`GET /api/address`

### Mục đích
Lấy danh sách tất cả địa chỉ của người dùng đã đăng nhập

### Authentication
Yêu cầu session hợp lệ (đã đăng nhập)

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/address \
  -H "Cookie: JSESSIONID=ABC123..."
```

### Success Response (200 OK)
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "addressLine": "123 Nguyễn Huệ, Phường Bến Thành",
    "city": "Quận 1",
    "district": "TP. Hồ Chí Minh",
    "ward": "Bến Thành",
    "isDefault": true,
    "userId": "789e0123-f45g-67h8-i901-234567890123"
  }
]
```

---

## 2. Add New Address

### Endpoint
`POST /api/address`

### Mục đích
Thêm địa chỉ mới cho người dùng đã đăng nhập

### Authentication
Yêu cầu session hợp lệ (đã đăng nhập)

### Request Body
```json
{
  "addressLine": "123 Nguyễn Huệ, Phường Bến Thành",
  "city": "Quận 1",
  "district": "TP. Hồ Chí Minh",
  "ward": "Bến Thành"
}
```

### Validation Rules
- `addressLine`: Bắt buộc, tối đa 500 ký tự
- `city`: Bắt buộc, tối đa 255 ký tự
- `district`: Bắt buộc, tối đa 255 ký tự
- `ward`: Bắt buộc, tối đa 255 ký tự

### Endpoint Demo
```bash
curl -X POST http://localhost:8080/api/address \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=ABC123..." \
  -d '{
    "addressLine": "123 Nguyễn Huệ, Phường Bến Thành",
    "city": "Quận 1",
    "district": "TP. Hồ Chí Minh",
    "ward": "Bến Thành"
  }'
```

### Success Response (200 OK)
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "addressLine": "123 Nguyễn Huệ, Phường Bến Thành",
  "city": "Quận 1",
  "district": "TP. Hồ Chí Minh",
  "ward": "Bến Thành",
  "isDefault": false,
  "userId": "789e0123-f45g-67h8-i901-234567890123"
}
```

### Error Response (400 Bad Request)
```json
{
  "addressLine": "Address line is required",
  "city": "City is required",
  "district": "District is required",
  "ward": "Ward is required"
}
```

---

## 3. Update Address

### Endpoint
`PUT /api/address/{addressId}`

### Mục đích
Cập nhật thông tin địa chỉ hiện tại của người dùng đã đăng nhập

### Authentication
Yêu cầu session hợp lệ (đã đăng nhập)

### Path Parameters
- `addressId`: ID của địa chỉ cần cập nhật

### Request Body
```json
{
  "addressLine": "456 Nguyễn Văn Linh, Phường Bình Thọ",
  "city": "Quận 7",
  "district": "TP. Hồ Chí Minh",
  "ward": "Bình Thọ",
  "isDefault": true
}
```

### Validation Rules
- `addressLine`: Bắt buộc, tối đa 500 ký tự
- `city`: Bắt buộc, tối đa 255 ký tự
- `district`: Bắt buộc, tối đa 255 ký tự
- `ward`: Bắt buộc, tối đa 255 ký tự
- `isDefault`: Không bắt buộc, boolean

### Endpoint Demo
```bash
curl -X PUT http://localhost:8080/api/address/123e4567-e89b-12d3-a456-426614174000 \
  -H "Content-Type: application/json" \
  -H "Cookie: JSESSIONID=ABC123..." \
  -d '{
    "addressLine": "456 Nguyễn Văn Linh, Phường Bình Thọ",
    "city": "Quận 7",
    "district": "TP. Hồ Chí Minh",
    "ward": "Bình Thọ",
    "isDefault": true
  }'
```

### Success Response (200 OK)
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "addressLine": "456 Nguyễn Văn Linh, Phường Bình Thọ",
  "city": "Quận 7",
  "district": "TP. Hồ Chí Minh",
  "ward": "Bình Thọ",
  "isDefault": true,
  "userId": "789e0123-f45g-67h8-i901-234567890123"
}
```

### Error Response (404 Not Found)
```json
{
  "error": "Address not found or does not belong to user",
  "status": "NOT_FOUND"
}
```

### Error Response (400 Bad Request)
```json
{
  "addressLine": "Address line is required",
  "city": "City is required",
  "district": "District is required",
  "ward": "Ward is required"
}
```

---

## Shipping Methods APIs

### Overview
Quản lý phương thức vận chuyển với các thao tác lấy danh sách và chi tiết phương thức vận chuyển. Không yêu cầu authentication để truy cập.

---

## 1. Get All Active Shipping Methods

### Endpoint
`GET /api/shipping-methods`

### Mục đích
Lấy danh sách tất cả phương thức vận chuyển đang hoạt động

### Authentication
Không yêu cầu authentication

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/shipping-methods
```

### Success Response (200 OK)
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "code": "STANDARD",
    "name": "Giao hàng tiêu chuẩn",
    "description": "Giao hàng trong 3-5 ngày làm việc",
    "baseFee": 25000.000,
    "estimatedDaysMin": 3,
    "estimatedDaysMax": 5,
    "isActive": true,
    "createdAt": "2026-05-09T10:30:00"
  },
  {
    "id": "456e7890-f12c-34d5-b789-012345678901",
    "code": "EXPRESS",
    "name": "Giao hàng nhanh",
    "description": "Giao hàng trong 1-2 ngày làm việc",
    "baseFee": 50000.000,
    "estimatedDaysMin": 1,
    "estimatedDaysMax": 2,
    "isActive": true,
    "createdAt": "2026-05-09T10:30:00"
  }
]
```

---

## 2. Get Shipping Method by ID

### Endpoint
`GET /api/shipping-methods/{id}`

### Mục đích
Lấy thông tin chi tiết của một phương thức vận chuyển theo ID

### Authentication
Không yêu cầu authentication

### Path Parameters
- `id`: ID của phương thức vận chuyển

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/shipping-methods/123e4567-e89b-12d3-a456-426614174000
```

### Success Response (200 OK)
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "code": "STANDARD",
  "name": "Giao hàng tiêu chuẩn",
  "description": "Giao hàng trong 3-5 ngày làm việc",
  "baseFee": 25000.000,
  "estimatedDaysMin": 3,
  "estimatedDaysMax": 5,
  "isActive": true,
  "createdAt": "2026-05-09T10:30:00"
}
```

### Error Response (404 Not Found)
```json
{
  "error": "Shipping method not found with ID: 123e4567-e89b-12d3-a456-426614174000",
  "status": "NOT_FOUND"
}
```

---

## Payment Methods APIs

### Overview
Quản lý phương thức thanh toán với các thao tác lấy danh sách và chi tiết phương thức thanh toán. Không yêu cầu authentication để truy cập.

---

## 1. Get All Active Payment Methods

### Endpoint
`GET /api/payment-methods`

### Mục đích
Lấy danh sách tất cả phương thức thanh toán đang hoạt động

### Authentication
Không yêu cầu authentication

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/payment-methods
```

### Success Response (200 OK)
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "code": "CASH_ON_DELIVERY",
    "name": "Thanh toán khi nhận hàng (COD)",
    "isActive": true,
    "createdAt": "2026-05-09T10:30:00"
  },
  {
    "id": "456e7890-f12c-34d5-b789-012345678901",
    "code": "BANK_TRANSFER",
    "name": "Chuyển khoản ngân hàng",
    "isActive": true,
    "createdAt": "2026-05-09T10:30:00"
  },
  {
    "id": "789e0123-f45g-67h8-i901-234567890123",
    "code": "CREDIT_CARD",
    "name": "Thẻ tín dụng/Ghi nợ",
    "isActive": true,
    "createdAt": "2026-05-09T10:30:00"
  }
]
```

---

## 2. Get Payment Method by ID

### Endpoint
`GET /api/payment-methods/{id}`

### Mục đích
Lấy thông tin chi tiết của một phương thức thanh toán theo ID

### Authentication
Không yêu cầu authentication

### Path Parameters
- `id`: ID của phương thức thanh toán

### Endpoint Demo
```bash
curl -X GET http://localhost:8080/api/payment-methods/123e4567-e89b-12d3-a456-426614174000
```

### Success Response (200 OK)
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "code": "CASH_ON_DELIVERY",
  "name": "Thanh toán khi nhận hàng (COD)",
  "isActive": true,
  "createdAt": "2026-05-09T10:30:00"
}
```

### Error Response (404 Not Found)
```json
{
  "error": "Payment method not found with ID: 123e4567-e89b-12d3-a456-426614174000",
  "status": "NOT_FOUND"
}
```

---

## Error Handling

### Common HTTP Status Codes
- `200 OK`: Request thành công
- `401 Unauthorized`: Authentication failed hoặc không có session
- `400 Bad Request`: Validation error trong request body
- `404 Not Found`: Resource không tồn tại
- `500 Internal Server Error`: Server error

### Error Response Format
```json
{
  "error": "Error message",
  "status": "ERROR_CODE"
}
```
