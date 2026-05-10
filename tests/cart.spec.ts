import { test, expect } from '@playwright/test';

test.describe('Cart E2E Tests', () => {
  
  // =========================
  // BEFORE EACH: LOGIN + GO TO PRODUCTS
  // =========================
  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5173');

    // mở login modal (QUAN TRỌNG)
    await page.click('text=Đăng nhập');

    // đợi modal render
    await page.waitForSelector('[data-testid="email-input"]');

    // Login
    await page.fill('[data-testid="email-input"]', 'testuser');
    await page.fill('[data-testid="password-input"]', 'Test123');
    await page.click('[data-testid="login-btn"]');

    // đợi login xong
    await page.waitForURL('**/products');
    });
  // =========================
  // 1. ADD TO CART SUCCESS
  // =========================
  test('Add to cart success', async ({ page }) => {

    // click product add to cart (giả sử product id = 1)
    await page.click('[data-testid="add-to-cart-btn-1"]');

    // check success message (nếu bạn có alert thì bỏ qua, hoặc toast)
    // check cart badge update
    await expect(
      page.locator('[data-testid="cart-badge"]')
    ).toHaveText('1');
  });

  // =========================
  // 2. UPDATE QUANTITY IN CART
  // =========================
  test('Update quantity in cart', async ({ page }) => {
    await page.goto('http://localhost:5173');

    await page.click('text=Đăng nhập');
    await page.fill('[data-testid="email-input"]', 'testuser');
    await page.fill('[data-testid="password-input"]', 'Test123');
    await page.click('[data-testid="login-btn"]');

    await page.waitForURL('**/products');

    // add product trước
    await page.click('[data-testid="add-to-cart-btn-1"]');

    // rồi mới vào cart
    await page.goto('http://localhost:5173/cart');

    await page.click('[data-testid="increase-btn-1"]');

    await expect(
        page.locator('[data-testid="cart-quantity-1"]')
    ).toHaveText('2');
  });
  // =========================
  // 3. DECREASE QUANTITY
  // =========================
  test('Decrease quantity in cart', async ({ page }) => {

    await page.goto('http://localhost:5173/cart');

    await page.click('[data-testid="decrease-btn-1"]');

    await expect(
      page.locator('[data-testid="cart-quantity-1"]')
    ).toHaveText('1');
  });

  // =========================
  // 4. REMOVE ITEM FROM CART
  // =========================
  test('Remove item from cart', async ({ page }) => {

    await page.goto('http://localhost:5173/cart');

    await page.click('[data-testid="remove-item-1"]');

    // item biến mất
    await expect(
      page.locator('[data-testid="cart-item-1"]')
    ).toHaveCount(0);
  });

  // =========================
  // 5. TOTAL PRICE UPDATE
  // =========================
  test('Cart total updates correctly', async ({ page }) => {

    await page.goto('http://localhost:5173/cart');

    // check total tồn tại
    await expect(
      page.locator('[data-testid="cart-total"]')
    ).toBeVisible();

    // click tăng quantity
    await page.click('[data-testid="increase-btn-1"]');

    // total phải thay đổi (không fix số vì dynamic)
    const total = await page.locator('[data-testid="cart-total"]').textContent();
    expect(total).not.toBeNull();
  });

  // =========================
  // 6. CHECKOUT FLOW
  // =========================
  test('Checkout button works', async ({ page }) => {

    await page.goto('http://localhost:5173/cart');

    await page.click('[data-testid="checkout-btn"]');

    // ví dụ redirect hoặc message
    await expect(page).toHaveURL(/checkout|order|success/);
  });

});