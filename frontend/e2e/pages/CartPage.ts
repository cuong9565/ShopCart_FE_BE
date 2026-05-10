import { Page, Locator } from '@playwright/test';

export class CartPage {
  readonly page: Page;
  readonly cartItems: Locator;
  readonly totalPrice: Locator;
  readonly checkoutBtn: Locator;

  constructor(page: Page) {
    this.page = page;
    this.cartItems = page.locator('[data-testid="cart-item"]');
    this.totalPrice = page.locator('[data-testid="cart-total-price"]');
    this.checkoutBtn = page.locator('[data-testid="checkout-btn"]');
  }

  async goto() {
    await this.page.goto('/cart');
  }

  async getItemCount(): Promise<number> {
    return await this.cartItems.count();
  }

  async increaseQuantity(index: number = 0) {
    const item = this.cartItems.nth(index);
    await item.locator('[data-testid="increase-qty-btn"]').click();
  }

  async decreaseQuantity(index: number = 0) {
    const item = this.cartItems.nth(index);
    await item.locator('[data-testid="decrease-qty-btn"]').click();
  }

  async getQuantity(index: number = 0): Promise<string> {
    const item = this.cartItems.nth(index);
    return await item.locator('[data-testid="cart-item-qty"]').innerText();
  }

  async removeItem(index: number = 0) {
    const item = this.cartItems.nth(index);
    await item.locator('[data-testid="remove-item-btn"]').click();
  }

  async getTotalPrice(): Promise<string> {
    return await this.totalPrice.innerText();
  }

  async checkout() {
    await this.checkoutBtn.click();
  }
}
