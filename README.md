# ShopCart - E-commerce Testing Project

> **Bài Tập Lớn - Kiểm Thử Phần Mềm**
> 
> **Version**: 1.1 | **Niên khóa**: 2025-2026 | **GVHD**: Từ Lãng Phiêu
> 
> **Trường**: Đại học Sài Gòn - Khoa Công Nghệ Thông Tin
> 
> **Document created**: May 4, 2026
> **Course**: Kiểm Thử Phần Mềm (Software Testing)
> **Version**: 1.1

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Installation & Setup](#2-installation--setup)
3. [How to Run](#3-how-to-run)
4. [Testing](#4-testing)
5. [Assignment Requirements](#5-assignment-requirements)
6. [Team Members](#6-team-members)

---

## 1. Project Overview

### 1.1 Giới thiệu
Dự án ShopCart là một ứng dụng web thương mại điện tử phục vụ cho việc kiểm thử các chức năng ở cả frontend và backend. Đây là bài tập lớn môn Kiểm Thử Phần Mềm, tập trung vào việc áp dụng các kỹ thuật kiểm thử như Unit Testing, Integration Testing, và Mock Testing.

### 1.2 Bảng thông tin dự án

| Attribute        | Value                                                       |
| ---------------- | ----------------------------------------------------------- |
| **Project Name** | ShopCart                                                    |
| **Type**         | E-commerce Web Application                                  |
| **Mục đích**     | Bài tập lớn Kiểm Thử Phần Mềm                               |
| **Backend**      | Spring Boot 3.5.0 + Java 21                                 |
| **Frontend**     | React 19.x + Vite + TailwindCSS v4                          |
| **Database**     | Vercel Neon PostgreSQL                                      |
| **Testing**      | Vitest + Playwright (Frontend), JUnit 5 + Mockito (Backend) |

### 1.3 Các nghiệp vụ chính

#### 1.3.1 Quản lý Giỏ hàng (Cart Management)
- **Thêm vào giỏ:** Cho phép chọn sản phẩm và số lượng để thêm vào giỏ. Hệ thống phải kiểm tra tồn kho và tính hợp lệ của số lượng (>= 1).
- **Cập nhật số lượng:** Thay đổi số lượng trực tiếp trong giỏ hàng. Kiểm tra tồn kho thời gian thực.
- **Xóa sản phẩm:** Loại bỏ sản phẩm khỏi giỏ hàng.
- **Tính toán giỏ hàng:** Tự động tính tổng tiền (Subtotal) dựa trên đơn giá và số lượng.

#### 1.3.2 Quản lý Mua hàng (Purchase & Checkout)
- **Xác nhận đơn hàng:** Kiểm tra tồn kho lần cuối cho tất cả sản phẩm trong giỏ.
- **Áp dụng Coupon:** Nhập mã giảm giá để được giảm trừ theo % hoặc số tiền cố định.
- **Tính phí vận chuyển:** Cộng thêm phí giao hàng vào tổng hóa đơn.
- **Tạo đơn hàng:** Lưu thông tin đơn hàng vào hệ thống và trừ tồn kho tương ứng.

#### 1.3.3 Quản lý Kho (Inventory)
- **Kiểm tra tồn kho:** Cảnh báo hoặc khóa thao tác khi sản phẩm hết hàng hoặc số lượng yêu cầu vượt mức tồn.
- **Trừ kho:** Thực hiện trừ số lượng khi đơn hàng được tạo (status PENDING).
- **Hoàn kho:** Cộng lại số lượng vào kho khi đơn hàng bị hủy.

#### 1.3.4 Tính toán giá (Pricing)
- **Tính tổng giá:** Tính tổng giá trị đơn hàng hoặc giỏ hàng (đơn giá × số lượng).
- **Áp dụng giảm giá:** Hỗ trợ mã giảm giá theo % hoặc số tiền cố định.
- **Phí vận chuyển:** Cộng phí vận chuyển khi phát sinh.

---

## 2. Installation & Setup

### 2.1 Backend Setup

```bash
cd backend

# Using Maven
mvn clean install

# Or using Maven Wrapper
./mvnw clean install
```

**Backend Configuration:**
- Database: PostgreSQL (Vercel Neon)
- Port: 8080
- Framework: Spring Boot 3.5.0

### 2.2 Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Or using yarn
yarn install
```


## 3. How to Run
### 3.1 Backend

```bash
cd backend

# Using Maven
mvn spring-boot:run

# Or build and run
./mvnw clean package
java -jar target/shopcart-backend-1.0.0.jar
```

**Backend URL**: `http://localhost:8080`

### 3.2 Frontend

```bash
cd frontend

# Run development server
npm run dev

# Or build and preview
npm run build
npm run preview
```

**Frontend URL**: `http://localhost:5173`

---

## 4. Testing

### 4.1 Frontend Testing

```bash
# Run unit tests
npm run test

# Run tests with coverage
npm run test:coverage

# Run E2E tests
npm run e2e
```

### 4.2 Backend Testing

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn test jacoco:report
```

---

## 5. Assignment Requirements

### 5.1 Bài tập lớn môn Kiểm Thử Phần Mềm
- **Câu 1:** Phân tích và Thiết kế Test Cases (0.5 điểm)
- **Câu 2:** Unit Testing và Test-Driven Development (2 điểm)
- **Câu 3:** Integration Testing (2 điểm)
- **Câu 4:** Mock Testing (2 điểm)

### 5.2 Coverage Targets
- **Frontend:** ≥ 90% cho validation modules
- **Backend:** ≥ 85% cho service layers

---

## 6. Team Members

- **Minh Phúc** - Thanh toán - Frontend
- **Đạt** - Giỏ hàng - Frontend  
- **Đăng Phúc** - Backend Development
- **Cường** - Backend Development

**Giảng viên hướng dẫn:** Từ Lãng Phiêu
**Khoa:** Công Nghệ Thông Tin
**Trường:** Đại học Sài Gòn

---

**Document Version**: 1.1
**Last Updated**: May 4, 2026