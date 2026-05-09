# ShopCart Backend API Documentation

## Table of Contents
1. [Authentication APIs](#authentication-apis)
2. [Category APIs](#category-apis)
3. [Product APIs](#product-apis)

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
