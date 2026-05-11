import { Page, Locator } from '@playwright/test';

export class CheckoutPage {
  readonly page: Page;
  readonly cartBadge: Locator;
  readonly checkoutBtn: Locator;
  readonly placeOrderBtn: Locator;
  readonly totalDisplay: Locator;
  readonly successMessage: Locator;
  readonly inventoryWarning: Locator;

  constructor(page: Page) {
    this.page = page;
    this.cartBadge = page.locator('[data-testid="cart-badge"]');
    this.checkoutBtn = page.locator('[data-testid="checkout-btn"]');
    this.placeOrderBtn = page.locator('#place-order-btn');
    this.totalDisplay = page.locator('span.text-primary.text-lg');
    this.successMessage = page.locator('[data-testid="order-success"]');
    this.inventoryWarning = page.locator('[data-testid="stock-warnings-container"]');
  }

  async goToCheckout() {
    // Click the checkout button from cart page or navigate directly
    if (await this.checkoutBtn.isVisible()) {
      await this.checkoutBtn.click();
    } else {
      await this.page.goto('/checkout');
    }
  }

  async fillRecipientInfo(name: string, phone: string) {
    await this.page.fill('#shippingFullName', name);
    await this.page.fill('#shippingPhone', phone);
  }

  async selectFirstAddress() {
    const radio = this.page.locator('input[name="address"]').first();
    await radio.waitFor({ state: 'visible' });
    await radio.click();
  }

  async selectFirstShipping() {
    const radio = this.page.locator('input[name="shipping"]').first();
    await radio.waitFor({ state: 'visible' });
    await radio.click();
  }

  async selectFirstPayment() {
    const radio = this.page.locator('input[name="payment"]').first();
    await radio.waitFor({ state: 'visible' });
    await radio.click();
  }

  async selectCoupon(code: string) {
    // Open accordion
    const accordionBtn = this.page.locator('[data-testid="coupon-accordion-btn"]').first();
    await accordionBtn.click();
    // Click coupon text item
    const couponItem = this.page.locator(`[data-testid="coupon-item-${code}"]`).first();
    await couponItem.click();
  }

  async placeOrder() {
    await this.placeOrderBtn.click();
    await this.successMessage.waitFor();
  }

  async getTotalPrice(): Promise<string> {
    return await this.totalDisplay.innerText();
  }
}

export default CheckoutPage;
