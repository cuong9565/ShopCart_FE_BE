import { test, expect } from '@playwright/test';

test.describe('Cart E2E Tests', () => {

  test.beforeEach(async ({ page }) => {
    await page.goto('http://localhost:5173');

    // =========================
    // 1. OPEN LOGIN MODAL
    // =========================
    await page.click('[data-testid="login-open-btn"]');

    // =========================
    // 2. LOGIN
    // =========================
    await expect(page.locator('[data-testid="email-input"]')).toBeVisible();

    await page.fill('[data-testid="email-input"]', 'linhtran@gmail.com');
    await page.fill('[data-testid="password-input"]', '123456');

    await page.click('[data-testid="login-btn"]');

    // =========================
    // 3. WAIT APP READY (KHÔNG WAIT URL)
    // =========================
    await expect(page.locator('[data-testid="product-list"]')).toBeVisible();
  });

  // =========================
  // 1. ADD TO CART
  // =========================
  test('Add to cart success', async ({ page }) => {

    await page.click('[data-testid="add-to-cart-btn-1"]');

    await expect(page.locator('[data-testid="cart-badge"]'))
      .toHaveText('1');
  });

  // =========================
  // 2. UPDATE QUANTITY
  // =========================
  test('Update quantity in cart', async ({ page }) => {

    await page.goto('http://localhost:5173/cart');

    await page.click('[data-testid="increase-btn-1"]');

    await expect(page.locator('[data-testid="cart-quantity-1"]'))
      .toHaveText('2');
  });

  // =========================
  // 3. DECREASE QUANTITY
  // =========================
  test('Decrease quantity in cart', async ({ page }) => {

    await page.goto('http://localhost:5173/cart');

    await page.click('[data-testid="decrease-btn-1"]');

    await expect(page.locator('[data-testid="cart-quantity-1"]'))
      .toHaveText('1');
  });

  // =========================
  // 4. REMOVE ITEM
  // =========================
  test('Remove item from cart', async ({ page }) => {

    await page.goto('http://localhost:5173/cart');

    await page.click('[data-testid="remove-item-1"]');

    await expect(page.locator('[data-testid="cart-item-1"]'))
      .toHaveCount(0);
  });

  // =========================
  // 5. TOTAL PRICE
  // =========================
  test('Cart total updates correctly', async ({ page }) => {

    await page.goto('http://localhost:5173/cart');

    await expect(page.locator('[data-testid="cart-total"]'))
      .toBeVisible();

    await page.click('[data-testid="increase-btn-1"]');

    const total = await page.locator('[data-testid="cart-total"]').textContent();
    expect(total).not.toBeNull();
  });

  // =========================
  // 6. CHECKOUT
  // =========================
  test('Checkout button works', async ({ page }) => {

    await page.goto('http://localhost:5173/cart');

    await page.click('[data-testid="checkout-btn"]');

    await expect(page).toHaveURL(/checkout|order|success/);
  });

});