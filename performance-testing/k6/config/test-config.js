// Test Configuration for ShopCart Performance Testing
export const CONFIG = {
  // Base URL for the application
  BASE_URL: 'http://localhost:8080',
  
  // Test user credentials
  TEST_USER: {
    username: 'linhtran@gmail.com',
    password: '123456'
  },
  
  // Performance test scenarios
  SCENARIOS: {
    // Cart operations test
    CART_TEST: {
      name: 'Cart Operations Load Test',
      vus: 10,           // Virtual users
      duration: '10s',    // Test duration
      thresholds: {
        http_req_duration: ['p(95)<500'], // 95% of requests under 500ms
        http_req_failed: ['rate<0.05'],   // Error rate under 5%
        http_reqs: ['rate>10'],           // At least 10 requests per second
      }
    },
    
    // Checkout process test
    CHECKOUT_TEST: {
      name: 'Checkout Process Performance Test',
      vus: 30,           // Virtual users
      duration: '3m',    // Test duration
      thresholds: {
        http_req_duration: ['p(95)<1000'], // 95% of requests under 1s
        http_req_failed: ['rate<0.02'],   // Error rate under 2%
        http_reqs: ['rate>5'],            // At least 5 requests per second
      }
    },
    
    // Stress test
    STRESS_TEST: {
      name: 'Stress Test - Peak Load',
      stages: [
        { duration: '1m', target: 20 },   // Ramp up to 20 users
        { duration: '2m', target: 50 },   // Ramp up to 50 users
        { duration: '3m', target: 100 },  // Peak load: 100 users
        { duration: '2m', target: 50 },   // Scale down to 50 users
        { duration: '1m', target: 0 },     // Ramp down to 0 users
      ],
      thresholds: {
        http_req_duration: ['p(95)<2000'], // 95% under 2s during stress
        http_req_failed: ['rate<0.10'],    // Error rate under 10%
      }
    },
    
    // Soak test (endurance)
    SOAK_TEST: {
      name: 'Endurance Test',
      vus: 20,           // Sustained load
      duration: '10m',   // Extended test duration
      thresholds: {
        http_req_duration: ['p(95)<800'],  // 95% under 800ms
        http_req_failed: ['rate<0.03'],    // Error rate under 3%
      }
    }
  },
  
  // API endpoints
  ENDPOINTS: {
    LOGIN: '/api/auth/login',
    CART: '/api/cart',
    CART_PRICING: '/api/cart/pricing',
    ORDERS: '/api/orders',
    PRODUCTS: '/api/products'
  },
  
  // Test data
  TEST_DATA: {
    products: [
      { productId: '08f10b51-f4b1-469c-b43d-e26ffa69ca9b', quantity: 2 },
      { productId: '1921ef85-bfca-4fb4-a496-57ff4a201ddb', quantity: 1 },
      { productId: '3e506f92-eb9c-49ec-9f0a-9588ae7dee33', quantity: 3 }
    ],
    coupons: ['43844029-0af4-4249-adc7-5adf85c61d66', 
      '438616d1-5a0e-447c-a7e7-b02f3f079dfa', 
      '4e3a24aa-19f2-4cb1-8edc-de7534ee0604'
    ],
    shippingMethods: ['445b1be6-2438-439e-a563-996caae70f69', 
      '57fc2e81-e489-4836-a103-eedfc135a6a1', 
      'd1b92c81-95d9-4d5e-988b-60f0ede54d00'
    ],
    paymentMethods: ['02d603c0-c92a-447e-880b-d7951eeacc05', 
      '76a5feca-8970-4d04-955b-7745c47d7ecb', 
      '83774bff-0331-45f8-b121-06d0174e998a', 
      'b2193f42-dbdc-4a2c-971d-bc99732a88d3', 
      'd4e97d96-58fb-4f1e-9fb9-6aed72440bf5'
    ]
  }
};
