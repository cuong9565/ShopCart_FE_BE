# ShopCart Performance Testing Suite

This directory contains comprehensive performance tests for the ShopCart application using k6.

## 📋 Test Scenarios

### a) Kịch bản tải đã xây dựng

#### 1. Cart Performance Test
- **API được test**: `/api/cart`, `/api/cart/pricing`
- **Số lượng người dùng đồng thời**: 50 VUs
- **Thời lượng chạy test**: 2 phút
- **Các chỉ số cần đo**:
  - Response time (p95 < 500ms)
  - Error rate (< 5%)
  - Throughput (> 10 requests/second)
  - Cart operations success rate

#### 2. Checkout Performance Test
- **API được test**: `/api/orders` (place order)
- **Số lượng người dùng đồng thời**: 30 VUs
- **Thời lượng chạy test**: 3 phút
- **Các chỉ số cần đo**:
  - Response time (p95 < 1000ms)
  - Error rate (< 2%)
  - Throughput (> 5 requests/second)
  - Order creation success rate

#### 3. Stress Test
- **API được test**: Tất cả Cart và Order APIs
- **Load progression**:
  - 1 phút: Ramp up đến 20 users
  - 2 phút: Ramp up đến 50 users
  - 3 phút: Peak load 100 users
  - 2 phút: Scale down đến 50 users
  - 1 phút: Ramp down đến 0 users
- **Các chỉ số cần đo**:
  - Response time (p95 < 2000ms)
  - Error rate (< 10%)
  - System behavior under peak load

## 🚀 Quick Start

### Prerequisites
1. Install k6: `npm install -g k6`
2. Start the backend application: `cd backend && mvn spring-boot:run`
3. Ensure test user exists in the database

### Running Tests

```bash
# Install dependencies
npm install

# Run cart performance test
npm run test:cart

# Run checkout performance test
npm run test:checkout

# Run stress test
npm run test:stress

# Generate HTML report
npm run report:html
```

## 📊 Metrics Collected

### b) Các chỉ số chính được thu thập

#### Response Time Metrics
- **Average Response Time**: Thời gian phản hồi trung bình
- **95th Percentile (p95)**: 95% requests có thời gian phản hồi dưới giá trị này
- **Maximum Response Time**: Thời gian phản hồi cao nhất
- **Minimum Response Time**: Thời gian phản hồi thấp nhất

#### Throughput Metrics
- **Requests per Second**: Số lượng request mỗi giây
- **Successful Requests Rate**: Tỷ lệ request thành công
- **Failed Requests Rate**: Tỷ lệ request thất bại

#### Error Metrics
- **HTTP Error Rate**: Tỷ lệ lỗi HTTP (4xx, 5xx)
- **Business Logic Errors**: Lỗi validation, business logic
- **Authentication Errors**: Lỗi xác thực

#### Resource Metrics
- **Memory Usage**: Mức sử dụng bộ nhớ
- **CPU Usage**: Mức sử dụng CPU
- **Database Connection Pool**: Số kết nối database

## 📁 File Structure

```
performance-testing/k6/
├── config/
│   └── test-config.js          # Test configuration and scenarios
├── utils/
│   └── auth-helper.js           # Authentication utilities
├── tests/
│   ├── cart-performance-test.js # Cart operations performance test
│   ├── checkout-performance-test.js # Checkout process test
│   └── stress-test.js           # Stress test with staged load
├── package.json                 # NPM configuration
└── README.md                    # This file
```

## 🔧 Configuration

### Test Configuration (config/test-config.js)
- **BASE_URL**: URL của ứng dụng (default: http://localhost:8080)
- **TEST_USER**: Thông tin user test
- **SCENARIOS**: Cấu hình các kịch bản test
- **ENDPOINTS**: API endpoints
- **TEST_DATA**: Dữ liệu test (products, coupons, etc.)

### Thresholds Configuration
```javascript
thresholds: {
  http_req_duration: ['p(95)<500'], // 95% requests under 500ms
  http_req_failed: ['rate<0.05'],   // Error rate under 5%
  http_reqs: ['rate>10'],           // At least 10 requests per second
}
```

## 📈 Test Results Analysis

### c) Phân tích kết quả và xác định điểm nghẽn

#### Key Performance Indicators
1. **Response Time Analysis**
   - Cart operations: Target < 500ms (p95)
   - Checkout operations: Target < 1000ms (p95)
   - Stress conditions: Target < 2000ms (p95)

2. **Throughput Analysis**
   - Normal load: > 10 requests/second
   - Peak load: Maintain > 5 requests/second
   - Error rate: < 5% normal, < 10% stress

3. **Bottleneck Identification**
   - Database query performance
   - Authentication token validation
   - Inventory checking
   - Payment processing
   - Email notification sending

#### Common Bottlenecks & Solutions

##### 1. Database Performance
**Symptoms**: High response times, connection pool exhaustion
**Solutions**:
- Add database indexes on frequently queried columns
- Implement connection pooling optimization
- Use database query caching
- Consider read replicas for read-heavy operations

##### 2. Authentication Overhead
**Symptoms**: Slow response times on authenticated endpoints
**Solutions**:
- Implement JWT token caching
- Use Redis for session storage
- Optimize token validation logic

##### 3. Inventory Management
**Symptoms**: Slow checkout process, locking issues
**Solutions**:
- Implement optimistic locking
- Use message queues for inventory updates
- Cache product availability

##### 4. External Service Dependencies
**Symptoms**: Timeouts, inconsistent response times
**Solutions**:
- Implement circuit breakers
- Add retry mechanisms with exponential backoff
- Cache external service responses

## 🎯 Optimization Recommendations

### Immediate Optimizations (0-2 weeks)
1. **Database Indexing**
   ```sql
   CREATE INDEX idx_cart_user_id ON cart_items(user_id);
   CREATE INDEX idx_orders_user_id ON orders(user_id);
   CREATE INDEX idx_order_items_order_id ON order_items(order_id);
   ```

2. **Connection Pool Optimization**
   ```properties
   spring.datasource.hikari.maximum-pool-size=20
   spring.datasource.hikari.minimum-idle=5
   spring.datasource.hikari.connection-timeout=30000
   ```

3. **Caching Strategy**
   ```java
   @Cacheable(value = "products", key = "#productId")
   public Product getProduct(UUID productId) { ... }
   ```

### Medium-term Optimizations (2-6 weeks)
1. **Implement Redis Caching**
2. **Database Query Optimization**
3. **Asynchronous Processing for Notifications**
4. **API Response Compression**

### Long-term Optimizations (6+ weeks)
1. **Microservices Architecture**
2. **Database Sharding**
3. **CDN Implementation**
4. **Load Balancing Setup**

## 📝 Test Reports

After running tests, HTML reports will be generated showing:
- Response time distributions
- Error rates over time
- Throughput metrics
- Resource utilization
- Performance trends

## 🐛 Troubleshooting

### Common Issues
1. **Authentication Failures**: Verify test user exists and credentials are correct
2. **Database Connection Issues**: Check database is running and accessible
3. **Port Conflicts**: Ensure backend is running on configured port
4. **Memory Issues**: Increase JVM heap size for extended tests

### Debug Mode
Run tests with additional logging:
```bash
k6 run --vus 10 --duration 30s tests/cart-performance-test.js --log-level debug
```
