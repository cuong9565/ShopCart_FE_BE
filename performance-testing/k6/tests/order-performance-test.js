import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';
import { CONFIG } from '../config/test-config.js';
import { AuthHelper } from '../utils/auth-helper.js';

// Custom metrics for order operations
const orderCreationRate = new Rate('order_creation_rate');
const orderResponseTime = new Trend('order_response_time');
const errorRate = new Rate('error_rate');
const orderCounter = new Counter('order_counter');

// Initialize auth helper
const auth = new AuthHelper();

// TODO: Fill in these UUIDs with actual values from your database
const UUID_ADDRESS = '762696ed-a90b-4c2a-aa61-7a6342e2cc38';           // Address ID that belongs to test user
const UUID_SHIPPING_STANDARD = 'd1b92c81-95d9-4d5e-988b-60f0ede54d00';   // Standard shipping method ID
const UUID_SHIPPING_EXPRESS = '445b1be6-2438-439e-a563-996caae70f69';     // Express shipping method ID
const UUID_PAYMENT_COD = 'd4e97d96-58fb-4f1e-9fb9-6aed72440bf5';               // COD payment method ID
const UUID_PAYMENT_BANK = '76a5feca-8970-4d04-955b-7745c47d7ecb';              // Bank transfer payment method ID
const UUID_COUPON_TEST = '43844029-0af4-4249-adc7-5adf85c61d66';                // Test coupon ID (optional)

// Test options
export const options = {
  scenarios: {
    order_operations: {
      executor: 'constant-vus',
      vus: CONFIG.SCENARIOS.CART_TEST.vus,
      duration: CONFIG.SCENARIOS.CART_TEST.duration,
      exec: 'orderOperations',
    },
  },
  thresholds: CONFIG.SCENARIOS.CART_TEST.thresholds,
};

// Main order operations test
export function setup() {
  console.log('Đang thiết lập xác thực cho kiểm tra hiệu năng đơn hàng...');
  return { authenticated: true };
}

export function orderOperations() {
  // Each VU needs to authenticate separately
  const loginSuccess = auth.login();
  if (!loginSuccess) {
    console.error('Đăng nhập thất bại, bỏ qua thao tác này');
    return;
  }
  
  const authParams = auth.getAuthParams();
  
  // Randomly select order operation to test
  const operations = [
    'placeOrder',
    'placeOrderWithCoupon',
    'placeOrderWithDifferentShipping'
  ];
  
  const randomOperation = operations[Math.floor(Math.random() * operations.length)];
  
  switch (randomOperation) {
    case 'placeOrder':
      testPlaceOrder(authParams);
      break;
    case 'placeOrderWithCoupon':
      testPlaceOrderWithCoupon(authParams);
      break;
    case 'placeOrderWithDifferentShipping':
      testPlaceOrderWithDifferentShipping(authParams);
      break;
  }
  
  orderCounter.add(1);
  sleep(Math.random() * 3 + 2); // Random sleep between 2-5 seconds
}

// Test 1: Place Order
function testPlaceOrder(authParams) {
  // First add items to cart
  const addSuccess = addItemsToCart(authParams);
  if (!addSuccess) {
    console.error('Không thể thêm sản phẩm vào giỏ hàng, bỏ qua đặt hàng');
    return;
  }
  
  const payload = JSON.stringify({
    addressId: UUID_ADDRESS,
    shippingMethodId: UUID_SHIPPING_STANDARD,
    paymentMethodId: UUID_PAYMENT_COD,
    shippingFullName: 'Nguyễn Văn Test',
    shippingPhone: '0912345678'
  });
  
  const response = http.post(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.ORDERS}`, payload, authParams);
  
  // Debug response structure
  console.log(`Place Order Response Status: ${response.status}`);
  console.log(`Place Order Response Body: ${response.body}`);
  
  const success = check(response, {
    'place order status is 200': (r) => r.status === 200,
    'place order response time < 1000ms': (r) => r.timings.duration < 1000,
    'place order returns order id': (r) => r.json('id') !== undefined,
    'place order returns status': (r) => r.json('status') !== undefined,
    'place order returns shipping info': (r) => r.json('shippingInfo') !== undefined,
    'place order returns payment info': (r) => r.json('paymentInfo') !== undefined,
    'place order returns items': (r) => Array.isArray(r.json('items')),
    'place order returns pricing info': (r) => r.json('pricingInfo') !== undefined,
  });
  
  orderResponseTime.add(response.timings.duration);
  orderCreationRate.add(success);
  errorRate.add(!success);
  
  if (success) {
    console.log(`Đặt hàng thành công: ${response.json('id')}`);
  } else {
    console.error(`Đặt hàng thất bại: ${response.status} ${response.body}`);
  }
}

// Test 2: Place Order with Coupon
function testPlaceOrderWithCoupon(authParams) {
  // First add items to cart
  const addSuccess = addItemsToCart(authParams);
  if (!addSuccess) {
    console.error('Không thể thêm sản phẩm vào giỏ hàng, bỏ qua đặt hàng');
    return;
  }
  
  const payload = JSON.stringify({
    addressId: UUID_ADDRESS,
    shippingMethodId: UUID_SHIPPING_EXPRESS,
    paymentMethodId: UUID_PAYMENT_BANK,
    shippingFullName: 'Trần Thị Test',
    shippingPhone: '0987654321',
    couponIds: [UUID_COUPON_TEST]
  });
  
  const response = http.post(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.ORDERS}`, payload, authParams);
  
  // Debug response structure
  console.log(`Place Order with Coupon Response Status: ${response.status}`);
  console.log(`Place Order with Coupon Response Body: ${response.body}`);
  
  const success = check(response, {
    'place order with coupon status is 200': (r) => r.status === 200,
    'place order with coupon response time < 1200ms': (r) => r.timings.duration < 1200,
  });
  
  // Additional checks only if status is 200
  if (response.status === 200) {
    const structureSuccess = check(response, {
      'place order with coupon returns order id': (r) => r.json('id') !== undefined,
      'place order with coupon returns applied coupons': (r) => Array.isArray(r.json('appliedCoupons')),
    });
    
    if (!structureSuccess) {
      console.error('Order with coupon structure validation failed');
    }
  }
  
  orderResponseTime.add(response.timings.duration);
  orderCreationRate.add(success);
  errorRate.add(!success);
  
  if (success) {
    console.log(`Đặt hàng với coupon thành công: ${response.json('id')}`);
  } else {
    console.error(`Đặt hàng với coupon thất bại: ${response.status} ${response.body}`);
  }
}

// Test 3: Place Order with Different Shipping Method
function testPlaceOrderWithDifferentShipping(authParams) {
  // First add items to cart
  const addSuccess = addItemsToCart(authParams);
  if (!addSuccess) {
    console.error('Không thể thêm sản phẩm vào giỏ hàng, bỏ qua đặt hàng');
    return;
  }
  
  const payload = JSON.stringify({
    addressId: UUID_ADDRESS,
    shippingMethodId: UUID_SHIPPING_EXPRESS,
    paymentMethodId: UUID_PAYMENT_BANK,
    shippingFullName: 'Lê Văn Test',
    shippingPhone: '0911222333'
  });
  
  const response = http.post(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.ORDERS}`, payload, authParams);
  
  // Debug response structure
  console.log(`Place Order with Different Shipping Response Status: ${response.status}`);
  console.log(`Place Order with Different Shipping Response Body: ${response.body}`);
  
  const success = check(response, {
    'place order with different shipping status is 200': (r) => r.status === 200,
    'place order with different shipping response time < 1000ms': (r) => r.timings.duration < 1000,
  });
  
  // Additional checks only if status is 200
  if (response.status === 200) {
    const structureSuccess = check(response, {
      'place order with different shipping returns order id': (r) => r.json('id') !== undefined,
      'place order with different shipping returns shipping method': (r) => r.json('shippingInfo.methodName') !== undefined,
    });
    
    if (!structureSuccess) {
      console.error('Order with different shipping structure validation failed');
    }
  }
  
  orderResponseTime.add(response.timings.duration);
  orderCreationRate.add(success);
  errorRate.add(!success);
  
  if (success) {
    console.log(`Đặt hàng với shipping khác thành công: ${response.json('id')}`);
  } else {
    console.error(`Đặt hàng với shipping khác thất bại: ${response.status} ${response.body}`);
  }
}

// Helper function to add items to cart before placing order
function addItemsToCart(authParams) {
  const randomProduct = CONFIG.TEST_DATA.products[Math.floor(Math.random() * CONFIG.TEST_DATA.products.length)];
  const payload = JSON.stringify({
    productId: randomProduct.productId,
    quantity: Math.floor(Math.random() * 2) + 1 // Random quantity 1-2
  });
  
  const response = http.post(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, payload, authParams);
  
  return check(response, {
    'add to cart for order status is 200': (r) => r.status === 200,
    'add to cart for order returns cart item': (r) => r.json('id') !== undefined,
  });
}

export function teardown() {
  console.log('Kết thúc kiểm tra hiệu năng đơn hàng');
  
  // Save results to file
  const results = {
    timestamp: new Date().toISOString(),
    testType: 'Order Performance Test',
    duration: '2 minutes',
    virtualUsers: 50,
    summary: {
      orderCreationSuccessRate: (orderCreationRate.rate * 100).toFixed(2),
      overallErrorRate: (errorRate.rate * 100).toFixed(2),
      averageResponseTime: orderResponseTime.avg.toFixed(2),
      totalOrders: orderCounter.count
    }
  };
  
  // Write to results file
  const fs = require('fs');
  fs.writeFileSync('order-test-results.json', JSON.stringify(results, null, 2));
  console.log('Đã lưu kết quả vào file order-test-results.json');
}
