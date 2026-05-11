import http from 'k6/http';
import { check } from 'k6';
import { CONFIG } from '../config/test-config.js';

// Authentication helper functions
export class AuthHelper {
  constructor() {
    this.token = null;
    this.userId = null;
    this.email = null;
    this.sessionCookies = null;
  }

  // Login and get authentication session
  login() {
    const payload = JSON.stringify({
      email: CONFIG.TEST_USER.username,
      password: CONFIG.TEST_USER.password
    });

    const params = {
      headers: {
        'Content-Type': 'application/json',
      },
    };

    const response = http.post(`${CONFIG.BASE_URL}${CONFIG.ENDPOINTS.LOGIN}`, payload, params);
    
    const success = check(response, {
      'login successful': (r) => r.status === 200,
      'email received': (r) => r.json('email') !== undefined,
      'user id received': (r) => r.json('userId') !== undefined,
    });

    if (success) {
      this.userId = response.json('userId');
      this.email = response.json('email');
      // Store session cookies for future requests
      this.sessionCookies = response.cookies;
      console.log(`Đăng nhập thành công cho user: ${CONFIG.TEST_USER.username}`);
    } else {
      console.error(`Đăng nhập thất bại: ${response.status} ${response.body}`);
    }

    return success;
  }

  // Get authenticated headers
  getAuthHeaders() {
    if (!this.sessionCookies) {
      throw new Error('No session available. Call login() first.');
    }
    
    return {
      'Content-Type': 'application/json',
    };
  }

  // Get request params with session cookies
  getAuthParams() {
    if (!this.sessionCookies) {
      throw new Error('No session available. Call login() first.');
    }
    
    return {
      headers: {
        'Content-Type': 'application/json',
      },
      cookies: this.sessionCookies,
    };
  }

  // Get current user ID
  getUserId() {
    if (!this.userId) {
      throw new Error('No user ID available. Call login() first.');
    }
    return this.userId;
  }

  // Check if authenticated
  isAuthenticated() {
    return this.token !== null && this.userId !== null;
  }
}

// Global auth instance
export const auth = new AuthHelper();
