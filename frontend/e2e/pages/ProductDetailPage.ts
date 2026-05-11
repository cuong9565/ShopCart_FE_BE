import { Page, Locator } from '@playwright/test';

export class ProductDetailPage {
  readonly page: Page;
  readonly quantityInput: Locator;
  readonly addToCartBtn: Locator;
  readonly successToast: Locator;
  readonly errorToast: Locator;
  readonly cartBadge: Locator;

  constructor(page: Page) {
    this.page = page;
    this.quantityInput = page.locator('[data-testid="quantity-input"]');
    this.addToCartBtn = page.locator('[data-testid="add-to-cart-btn"]');
    this.successToast = page.locator('[data-testid="success-toast"]');
    this.errorToast = page.locator('[data-testid="error-toast"]');
    this.cartBadge = page.locator('[data-testid="cart-badge"]');
  }

  async gotoProduct(id: string, slug: string = 'product') {
    await this.page.goto(`/product/${slug}/${id}`);
  }

  async setQuantity(qty: number) {
    await this.quantityInput.fill(qty.toString());
  }

  async addToCart() {
    await this.addToCartBtn.click();
  }
}
