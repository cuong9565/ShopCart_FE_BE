# Cart Performance Tests

This document describes the comprehensive k6 performance tests for ShopCart cart operations.

## Overview

The cart performance tests cover all major cart API endpoints:
- **Add Product to Cart** (`POST /api/cart`)
- **Get Cart Items** (`GET /api/cart`)
- **Update Cart Item Quantity** (`PUT /api/cart`)
- **Remove Product from Cart** (`DELETE /api/cart`)
- **Get Cart Total Amount** (`GET /api/cart/total`)
- **Calculate Cart Pricing** (`POST /api/cart/pricing`)

## Test Features

### Custom Metrics
- **Response Time Tracking**: Individual response times for each cart operation
- **Success Rate Monitoring**: Success rates for each operation type
- **Error Rate Tracking**: Overall error rate across all operations
- **Operation Counter**: Total number of cart operations performed

### Test Scenarios
- **Load Testing**: Constant virtual users with sustained load
- **Random Operations**: Tests randomly select different cart operations to simulate real usage
- **Data Validation**: Comprehensive response validation for each API call
- **Error Handling**: Proper error logging and tracking

## Prerequisites

1. **Backend Server**: Ensure the ShopCart backend is running on `http://localhost:8080`
2. **Test User**: Configure test user credentials in `config/test-config.js`
3. **Test Data**: Update product IDs and other test data in the configuration file

## Configuration

### Test Configuration (`config/test-config.js`)

```javascript
// Cart test configuration
CART_TEST: {
  name: 'Cart Operations Load Test',
  vus: 50,           // Virtual users
  duration: '2m',    // Test duration
  thresholds: {
    http_req_duration: ['p(95)<500'], // 95% of requests under 500ms
    http_req_failed: ['rate<0.05'],   // Error rate under 5%
    http_reqs: ['rate>10'],           // At least 10 requests per second
  }
}
```

### Test Data

Update the `TEST_DATA` section with valid product IDs and other test data:

```javascript
TEST_DATA: {
  products: [
    { productId: '550e8400-e29b-41d4-a716-446655440001', quantity: 2 },
    { productId: '550e8400-e29b-41d4-a716-446655440002', quantity: 1 },
    { productId: '550e8400-e29b-41d4-a716-446655440003', quantity: 3 }
  ],
  coupons: ['SAVE10', 'FREESHIP', 'SAVE20'],
  shippingMethods: ['STANDARD', 'EXPRESS', 'OVERNIGHT']
}
```

## Running Tests

### Basic Cart Performance Test

```bash
# Navigate to the k6 directory
cd performance-testing/k6

# Run cart performance test
npm run test:cart

# Or run directly with k6
k6 run tests/cart-performance-test.js
```

### Generate HTML Report

```bash
# Generate HTML report for cart tests
npm run report:cart

# This will create cart-report.html in the current directory
```

### Custom Test Parameters

```bash
# Run with custom parameters
k6 run --vus 100 --duration 5m tests/cart-performance-test.js

# Run with specific thresholds
k6 run --threshold http_req_duration['p(95)']<300 tests/cart-performance-test.js
```

## Test Operations Explained

### 1. Add Product to Cart
- **Endpoint**: `POST /api/cart`
- **Test Data**: Random product from test data with random quantity (1-3)
- **Validations**: Response status, response time, cart item structure
- **Metrics**: `add_product_response_time`, `add_product_success`

### 2. Get Cart Items
- **Endpoint**: `GET /api/cart`
- **Validations**: Response status, response time, array structure, item validation
- **Metrics**: `get_cart_response_time`, `get_cart_success`

### 3. Update Cart Item Quantity
- **Endpoint**: `PUT /api/cart`
- **Logic**: Gets current cart, selects random item, updates quantity (1-5)
- **Validations**: Response status, response time, quantity update, subtotal recalculation
- **Metrics**: `update_quantity_response_time`, `update_quantity_success`

### 4. Get Cart Total Amount
- **Endpoint**: `GET /api/cart/total`
- **Validations**: Response status, response time, numeric value, non-negative
- **Metrics**: `get_cart_total_response_time`, `get_cart_total_success`

### 5. Calculate Cart Pricing
- **Endpoint**: `POST /api/cart/pricing`
- **Test Data**: Random coupon and shipping method
- **Validations**: Response status, response time, pricing structure, valid amounts
- **Metrics**: `calculate_pricing_response_time`, `calculate_pricing_success`

### 6. Remove Product from Cart
- **Endpoint**: `DELETE /api/cart`
- **Logic**: Gets current cart, selects random item for removal
- **Validations**: Response status, response time
- **Metrics**: `remove_product_response_time`, `remove_product_success`

## Metrics and Thresholds

### Response Time Metrics
- `add_product_response_time`: Time taken to add products
- `update_quantity_response_time`: Time taken to update quantities
- `remove_product_response_time`: Time taken to remove products
- `get_cart_response_time`: Time taken to retrieve cart
- `get_cart_total_response_time`: Time taken to get cart total
- `calculate_pricing_response_time`: Time taken to calculate pricing

### Success Rate Metrics
- `add_product_success`: Success rate for add operations
- `update_quantity_success`: Success rate for update operations
- `remove_product_success`: Success rate for remove operations
- `get_cart_success`: Success rate for get cart operations
- `get_cart_total_success`: Success rate for get total operations
- `calculate_pricing_success`: Success rate for pricing operations

### Default Thresholds
- **95th percentile response time**: < 500ms
- **Error rate**: < 5%
- **Request rate**: > 10 requests per second

## Test Results

### Console Output
The test provides real-time feedback:
```
✅ Product added to cart: 550e8400-e29b-41d4-a716-446655440001, Quantity: 2
🛒 Cart retrieved successfully: 3 items
🔄 Quantity updated: 550e8400-e29b-41d4-a716-446655440002 -> 4
💰 Cart total: 1250.50
🧾 Pricing calculated: Total: 1275.50, Shipping: 25.00
🗑️ Product removed from cart: 550e8400-e29b-41d4-a716-446655440003
```

### Summary Statistics
At test completion, a summary is displayed:
```
📊 Test Summary:
- Add Product Success Rate: 98.50%
- Update Quantity Success Rate: 97.20%
- Remove Product Success Rate: 99.10%
- Get Cart Success Rate: 99.80%
- Get Cart Total Success Rate: 99.90%
- Calculate Pricing Success Rate: 96.80%
- Overall Error Rate: 2.10%
- Total Cart Operations: 1250
```

## Troubleshooting

### Common Issues

1. **Authentication Failures**
   - Check test user credentials in `config/test-config.js`
   - Ensure the backend server is running and accessible

2. **Product Not Found**
   - Verify product IDs in test data exist in the database
   - Check if products are active and have sufficient stock

3. **Empty Cart Issues**
   - The test automatically adds products if cart is empty
   - Ensure add operations are working correctly

4. **Performance Issues**
   - Check backend server performance and database connections
   - Monitor system resources during test execution

### Debug Mode

Add console logging for debugging:
```javascript
// In test functions
console.log(`Request payload: ${payload}`);
console.log(`Response: ${JSON.stringify(response.json())}`);
```

## Best Practices

1. **Test Data Management**: Keep test data up-to-date with your database
2. **Threshold Tuning**: Adjust thresholds based on your performance requirements
3. **Environment Separation**: Use different configurations for staging/production
4. **Regular Testing**: Schedule regular performance tests to catch regressions
5. **Result Analysis**: Review HTML reports for detailed performance insights

## Integration with CI/CD

### GitHub Actions Example
```yaml
name: Performance Tests
on: [push, pull_request]
jobs:
  performance:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup k6
        run: |
          sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys C5AD17C747E3415A3642D57D77C6C491D6AC1D69
          echo "deb https://dl.k6.io/deb stable main" | sudo tee /etc/apt/sources.list.d/k6.list
          sudo apt-get update
          sudo apt-get install k6
      - name: Run Cart Performance Tests
        run: |
          cd performance-testing/k6
          k6 run tests/cart-performance-test.js
```

## Extending Tests

To add new cart operations:

1. **Add new test function** in `cart-performance-test.js`
2. **Add custom metrics** for the new operation
3. **Update the operations array** to include the new test
4. **Add validations** specific to the new endpoint
5. **Update documentation** with the new operation details

Example:
```javascript
// New metric
export let newOperationResponseTime = new Trend('new_operation_response_time');

// New test function
function testNewOperation(headers) {
  const response = http.get(`${CONFIG.BASE_URL}/api/cart/new-endpoint`, { headers });
  
  const success = check(response, {
    'new operation status is 200': (r) => r.status === 200,
    'new operation response time < 300ms': (r) => r.timings.duration < 300,
  });
  
  newOperationResponseTime.add(response.timings.duration);
  errorRate.add(!success);
}
```
