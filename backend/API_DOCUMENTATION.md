# ShopCart Backend API Documentation

## Authentication APIs

### Overview
Backend sử dụng **Session-based Authentication** với Spring Boot Security. Session được tự động quản lý và gửi qua HTTP cookies.

---

## 1. Login API

### `POST /api/auth/login`

**Mục đích:** Xác thực người dùng và tạo session

**Test:**
```
POST /api/auth/login
{
  "email": "linhtran@gmail.com",
  "password": "123456"
}
```

**Validation Rules:**
- `email`: Bắt buộc, phải là email hợp lệ
- `password`: Bắt buộc, tối thiểu 6 ký tự

**Success Response (200 OK):**
```json
{
  "email": "user@example.com",
  "role": "ROLE_USER",
  "userId": "123e4567-e89b-12d3-a456-426614174000"
}
```

**Error Response (401 Unauthorized):**
```json
{
  "email": null,
  "role": null,
  "userId": null
}
```

---

## 2. Check Authentication Status API

### `GET /api/auth/check`

**Mục đích:** Kiểm tra trạng thái authentication hiện tại

**Test:**
```
GET /api/auth/check
```

**Success Response (200 OK):**
```json
{
  "email": "user@example.com",
  "role": "ROLE_USER",
  "userId": "123e4567-e89b-12d3-a456-426614174000"
}
```

**Error Response (401 Unauthorized):**
```json
"Not authenticated"
```

---

## 3. Logout API

### `POST /api/auth/logout`

**Mục đích:** Đăng xuất và invalidate session

**Test:**
```
POST /api/auth/logout
```

## Error Handling

### Common HTTP Status Codes
- `200 OK`: Request thành công
- `401 Unauthorized`: Authentication failed hoặc không có session
- `400 Bad Request`: Validation error trong request body
- `500 Internal Server Error`: Server error

### Error Response Format
```json
{
  "error": "Error message",
  "status": "ERROR_CODE"
}
```
