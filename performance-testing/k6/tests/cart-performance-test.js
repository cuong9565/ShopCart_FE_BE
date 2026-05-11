import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend, Counter } from 'k6/metrics';
import { CONFIG } from '../config/test-config.js';
import { auth } from '../utils/auth-helper.js';

// Custom metrics for cart operations
export let errorRate = new Rate('cart_errors');
export let addProductResponseTime = new Trend('add_product_response_time');
export let updateQuantityResponseTime = new Trend('update_quantity_response_time');
export let removeProductResponseTime = new Trend('remove_product_response_time');
export let getCartResponseTime = new Trend('get_cart_response_time');
export let getCartTotalResponseTime = new Trend('get_cart_total_response_time');
export let calculatePricingResponseTime = new Trend('calculate_pricing_response_time');

export let addProductSuccessRate = new Rate('add_product_success');
export let updateQuantitySuccessRate = new Rate('update_quantity_success');
export let removeProductSuccessRate = new Rate('remove_product_success');
export let getCartSuccessRate = new Rate('get_cart_success');
export let getCartTotalSuccessRate = new Rate('get_cart_total_success');
export let calculatePricingSuccessRate = new Rate('calculate_pricing_success');

export let cartOperationsCounter = new Counter('cart_operations_total');

// Test options
export let options = {
  scenarios: {
    cart_operations: {
      executor: 'constant-vus',
      vus: CONFIG.SCENARIOS.CART_TEST.vus,
      duration: CONFIG.SCENARIOS.CART_TEST.duration,
      exec: 'cartOperations',
    },
  },
  thresholds: CONFIG.SCENARIOS.CART_TEST.thresholds,
};

// Main cart operations test
export function setup() {
  console.log('Đang thiết lập xác thực cho kiểm tra hiệu năng giỏ hàng...');
  return { authenticated: true };
}

export function cartOperations() {
  // Each VU needs to authenticate separately
  const loginSuccess = auth.login();
  if (!loginSuccess) {
    console.error('Đăng nhập thất bại, bỏ qua thao tác này');
    return;
  }
  
  const authParams = auth.getAuthParams();
  
  // Randomly select cart operation to test
  const operations = [
    'addProduct',
    'getCart', 
    'updateQuantity',
    'getCartTotal',
    'calculatePricing',
    'removeProduct'
  ];
  
  const randomOperation = operations[Math.floor(Math.random() * operations.length)];
  
  switch (randomOperation) {
    case 'addProduct':
      testAddProduct(authParams);
      break;
    case 'getCart':
      testGetCart(authParams);
      break;
    case 'updateQuantity':
      testUpdateQuantity(authParams);
      break;
    case 'getCartTotal':
      testGetCartTotal(authParams);
      break;
    case 'calculatePricing':
      testCalculatePricing(authParams);
      break;
    case 'removeProduct':
      testRemoveProduct(authParams);
      break;
  }
  
  cartOperationsCounter.add(1);
  sleep(Math.random() * 2 + 1); // Random sleep between 1-3 seconds
}

// Test 1: Add Product to Cart
function testAddProduct(authParams) {
  const randomProduct = CONFIG.TEST_DATA.products[Math.floor(Math.random() * CONFIG.TEST_DATA.products.length)];
  const payload = JSON.stringify({
    productId: randomProduct.productId,
    quantity: Math.floor(Math.random() * 3) + 1 // Random quantity 1-3
  });
  
  const response = http.post(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, payload, authParams);
  
  // Debug response structure
  console.log(`Response status: ${response.status}`);
  console.log(`Response body: ${response.body}`);
  
  const success = check(response, {
    'add product status is 200': (r) => r.status === 200,
    'add product response time < 500ms': (r) => r.timings.duration < 500,
    'add product returns cart item': (r) => r.json('id') !== undefined,
    'add product returns product ID': (r) => r.json('productId') === randomProduct.productId,
    'add product returns quantity': (r) => r.json('quantity') !== undefined,
    'add product returns subtotal': (r) => r.json('subtotal') !== undefined,
  });
  
  addProductResponseTime.add(response.timings.duration);
  addProductSuccessRate.add(success);
  errorRate.add(!success);
  
  if (success) {
    console.log(`Thêm sản phẩm vào giỏ hàng thành công: ${randomProduct.productId}, Số lượng: ${response.json('quantity')}`);
  } else {
    console.error(`Thêm sản phẩm vào giỏ hàng thất bại: ${response.status} ${response.body}`);
  }
}

// Test 2: Get Cart Items
function testGetCart(authParams) {
  const response = http.get(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, authParams);
  
  const success = check(response, {
    'get cart status is 200': (r) => r.status === 200,
    'get cart response time < 300ms': (r) => r.timings.duration < 300,
    'get cart returns array': (r) => Array.isArray(r.json()),
  });
  
  getCartResponseTime.add(response.timings.duration);
  getCartSuccessRate.add(success);
  errorRate.add(!success);
  
  if (success) {
    const cartItems = response.json();
    console.log(`Lấy giỏ hàng thành công: ${cartItems.length} sản phẩm`);
    
    // Additional checks for cart items
    if (cartItems.length > 0) {
      check(response, {
        'cart items have valid structure': (r) => {
          const items = r.json();
          return items.every(item => 
            item.id && item.productId && item.productName && 
            item.productPrice !== undefined && item.quantity !== undefined
          );
        }
      });
    }
  } else {
    console.error(`Lấy giỏ hàng thất bại: ${response.status} ${response.body}`);
  }
}

// Test 3: Update Cart Item Quantity
function testUpdateQuantity(authParams) {
  // First get current cart to find items to update
  const cartResponse = http.get(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, authParams);
  
  if (cartResponse.status !== 200 || cartResponse.json().length === 0) {
    // Add a product first if cart is empty
    testAddProduct(authParams);
    sleep(1);
    
    // Retry getting cart
    const retryCartResponse = http.get(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, authParams);
    if (retryCartResponse.status !== 200 || retryCartResponse.json().length === 0) {
      return; // Skip update test if still no items
    }
  }
  
  const updatedCartResponse = http.get(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, authParams);
  const cartItems = updatedCartResponse.json();
  
  if (cartItems.length > 0) {
    const randomItem = cartItems[Math.floor(Math.random() * cartItems.length)];
    const newQuantity = Math.floor(Math.random() * 5) + 1; // Random quantity 1-5
    
    const payload = JSON.stringify({
      productId: randomItem.productId,
      quantity: newQuantity
    });
    
    const response = http.put(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, payload, authParams);
    
    const success = check(response, {
      'update quantity status is 200': (r) => r.status === 200,
      'update quantity response time < 500ms': (r) => r.timings.duration < 500,
      'update quantity returns cart item': (r) => r.json('id') !== undefined,
      'update quantity matches new quantity': (r) => r.json('quantity') === newQuantity,
      'update quantity recalculates subtotal': (r) => r.json('subtotal') !== undefined,
    });
    
    updateQuantityResponseTime.add(response.timings.duration);
    updateQuantitySuccessRate.add(success);
    errorRate.add(!success);
    
    if (success) {
      console.log(`Cập nhật số lượng thành công: ${randomItem.productId} -> ${newQuantity}`);
    } else {
      console.error(`Cập nhật số lượng thất bại: ${response.status} ${response.body}`);
    }
  }
}

// Test 4: Get Cart Total Amount
function testGetCartTotal(authParams) {
  const response = http.get(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}/total`, authParams);
  
  const success = check(response, {
    'get cart total status is 200': (r) => r.status === 200,
    'get cart total response time < 300ms': (r) => r.timings.duration < 300,
    'get cart total returns number': (r) => !isNaN(parseFloat(r.body)),
    'get cart total is non-negative': (r) => parseFloat(r.body) >= 0,
  });
  
  getCartTotalResponseTime.add(response.timings.duration);
  getCartTotalSuccessRate.add(success);
  errorRate.add(!success);
  
  if (success) {
    const total = parseFloat(response.body);
    console.log(`Tổng tiền giỏ hàng: ${total.toFixed(2)}`);
  } else {
    console.error(`Lấy tổng tiền giỏ hàng thất bại: ${response.status} ${response.body}`);
  }
}

// Test 5: Calculate Cart Pricing
function testCalculatePricing(authParams) {
  const randomCoupon = CONFIG.TEST_DATA.coupons[Math.floor(Math.random() * CONFIG.TEST_DATA.coupons.length)];
  const randomShipping = CONFIG.TEST_DATA.shippingMethods[Math.floor(Math.random() * CONFIG.TEST_DATA.shippingMethods.length)];
  
  const payload = JSON.stringify({
    couponCodes: [randomCoupon],
    shippingMethodId: randomShipping
  });
  
  const response = http.post(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART_PRICING}`, payload, authParams);
  
  const success = check(response, {
    'calculate pricing status is 200': (r) => r.status === 200,
    'calculate pricing response time < 800ms': (r) => r.timings.duration < 800,
    'calculate pricing returns total product amount': (r) => r.json('totalProductAmount') !== undefined,
    'calculate pricing returns final total amount': (r) => r.json('finalTotalAmount') !== undefined,
    'calculate pricing returns base shipping fee': (r) => r.json('baseShippingFee') !== undefined,
    'calculate pricing returns final shipping fee': (r) => r.json('finalShippingFee') !== undefined,
  });
  
  calculatePricingResponseTime.add(response.timings.duration);
  calculatePricingSuccessRate.add(success);
  errorRate.add(!success);
  
  if (success) {
    const pricing = response.json();
    console.log(`Tính giá thành công: Tổng: ${pricing.finalTotalAmount}, Phí vận chuyển: ${pricing.finalShippingFee}`);
    
    // Additional validation for pricing structure
    check(response, {
      'pricing amounts are valid numbers': (r) => {
        const p = r.json();
        return !isNaN(parseFloat(p.totalProductAmount)) && 
               !isNaN(parseFloat(p.finalTotalAmount)) &&
               !isNaN(parseFloat(p.baseShippingFee)) &&
               !isNaN(parseFloat(p.finalShippingFee));
      }
    });
  } else {
    console.error(`Tính giá thất bại: ${response.status} ${response.body}`);
  }
}

// Test 6: Remove Product from Cart
function testRemoveProduct(authParams) {
  // First get current cart to find items to remove
  const cartResponse = http.get(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, authParams);
  
  if (cartResponse.status !== 200 || cartResponse.json().length === 0) {
    // Add a product first if cart is empty
    testAddProduct(authParams);
    sleep(1);
    
    // Retry getting cart
    const retryCartResponse = http.get(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, authParams);
    if (retryCartResponse.status !== 200 || retryCartResponse.json().length === 0) {
      return; // Skip remove test if still no items
    }
  }
  
  const updatedCartResponse = http.get(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, authParams);
  const cartItems = updatedCartResponse.json();
  
  if (cartItems.length > 0) {
    const randomItem = cartItems[Math.floor(Math.random() * cartItems.length)];
    
    const payload = JSON.stringify({
      productId: randomItem.productId
    });
    
    const response = http.del(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.CART}`, payload, authParams);
    
    const success = check(response, {
      'remove product status is 200': (r) => r.status === 200,
      'remove product response time < 500ms': (r) => r.timings.duration < 500,
    });
    
    removeProductResponseTime.add(response.timings.duration);
    removeProductSuccessRate.add(success);
    errorRate.add(!success);
    
    if (success) {
      console.log(`Xóa sản phẩm khỏi giỏ hàng thành công: ${randomItem.productId}`);
    } else {
      console.error(`Xóa sản phẩm khỏi giỏ hàng thất bại: ${response.status} ${response.body}`);
    }
  }
}

export function teardown() {
  console.log('Kết thúc kiểm tra hiệu năng giỏ hàng');
  
  // Print summary statistics
  console.log('\nTổng kết kiểm tra:');
  console.log(`- Tỷ lệ thành công thêm sản phẩm: ${(addProductSuccessRate.rate * 100).toFixed(2)}%`);
  console.log(`- Tỷ lệ thành công cập nhật số lượng: ${(updateQuantitySuccessRate.rate * 100).toFixed(2)}%`);
  console.log(`- Tỷ lệ thành công xóa sản phẩm: ${(removeProductSuccessRate.rate * 100).toFixed(2)}%`);
  console.log(`- Tỷ lệ thành công lấy giỏ hàng: ${(getCartSuccessRate.rate * 100).toFixed(2)}%`);
  console.log(`- Tỷ lệ thành công lấy tổng tiền: ${(getCartTotalSuccessRate.rate * 100).toFixed(2)}%`);
  console.log(`- Tỷ lệ thành công tính giá: ${(calculatePricingSuccessRate.rate * 100).toFixed(2)}%`);
  console.log(`- Tỷ lệ lỗi tổng: ${(errorRate.rate * 100).toFixed(2)}%`);
  console.log(`- Tổng số thao tác giỏ hàng: ${cartOperationsCounter.count}`);
  
  // Save results to file
  const results = {
    timestamp: new Date().toISOString(),
    testType: 'Cart Performance Test',
    duration: '2 minutes',
    virtualUsers: 50,
    summary: {
      addProductSuccessRate: (addProductSuccessRate.rate * 100).toFixed(2),
      updateQuantitySuccessRate: (updateQuantitySuccessRate.rate * 100).toFixed(2),
      removeProductSuccessRate: (removeProductSuccessRate.rate * 100).toFixed(2),
      getCartSuccessRate: (getCartSuccessRate.rate * 100).toFixed(2),
      getCartTotalSuccessRate: (getCartTotalSuccessRate.rate * 100).toFixed(2),
      calculatePricingSuccessRate: (calculatePricingSuccessRate.rate * 100).toFixed(2),
      overallErrorRate: (errorRate.rate * 100).toFixed(2),
      totalOperations: cartOperationsCounter.count
    }
  };
  
  // Write to results file
  const fs = require('fs');
  fs.writeFileSync('cart-test-results.json', JSON.stringify(results, null, 2));
  console.log('Đã lưu kết quả vào file cart-test-results.json');
}
