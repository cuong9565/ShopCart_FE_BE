# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: frontend\e2e\specs\cart.spec.ts >> Cart E2E Tests >> TC3: Điều hướng giỏ hàng và tương tác tăng giảm, xóa sản phẩm
- Location: frontend\e2e\specs\cart.spec.ts:202:3

# Error details

```
Error: page.goto: Protocol error (Page.navigate): Cannot navigate to invalid URL
Call log:
  - navigating to "/", waiting until "load"

```

# Test source

```ts
  1  | import { Page, Locator } from '@playwright/test';
  2  | 
  3  | export class LoginPage {
  4  |   readonly page: Page;
  5  |   readonly openModalBtn: Locator;
  6  |   readonly usernameInput: Locator;
  7  |   readonly passwordInput: Locator;
  8  |   readonly loginBtn: Locator;
  9  | 
  10 |   constructor(page: Page) {
  11 |     this.page = page;
  12 |     this.openModalBtn = page.locator('[data-testid="open-login-modal-btn"]');
  13 |     this.usernameInput = page.locator('[data-testid="username-input"]');
  14 |     this.passwordInput = page.locator('[data-testid="password-input"]');
  15 |     this.loginBtn = page.locator('[data-testid="login-btn"]');
  16 |   }
  17 | 
  18 |   async goto() {
> 19 |     await this.page.goto('/');
     |                     ^ Error: page.goto: Protocol error (Page.navigate): Cannot navigate to invalid URL
  20 |   }
  21 | 
  22 |   async login(email: string, pass: string) {
  23 |     await this.openModalBtn.click();
  24 |     await this.usernameInput.fill(email);
  25 |     await this.passwordInput.fill(pass);
  26 |     await this.loginBtn.click();
  27 |   }
  28 | }
  29 | 
```