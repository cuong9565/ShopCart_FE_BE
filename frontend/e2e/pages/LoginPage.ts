import { Page, Locator } from '@playwright/test';

export class LoginPage {
  readonly page: Page;
  readonly openModalBtn: Locator;
  readonly usernameInput: Locator;
  readonly passwordInput: Locator;
  readonly loginBtn: Locator;

  constructor(page: Page) {
    this.page = page;
    this.openModalBtn = page.locator('[data-testid="open-login-modal-btn"]');
    this.usernameInput = page.locator('[data-testid="username-input"]');
    this.passwordInput = page.locator('[data-testid="password-input"]');
    this.loginBtn = page.locator('[data-testid="login-btn"]');
  }

  async goto() {
    await this.page.goto('/');
  }

  async login(email: string, pass: string) {
    await this.openModalBtn.click();
    await this.usernameInput.fill(email);
    await this.passwordInput.fill(pass);
    await this.loginBtn.click();
  }
}
