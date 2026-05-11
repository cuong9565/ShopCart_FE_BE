import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { ProductDetailPage } from '../pages/ProductDetailPage';
import { CartPage } from '../pages/CartPage';

let mockCart: any[] = [];
let loggedInUser: any = null;

test.describe('Cart E2E Tests', () => {
  test.beforeEach(async ({ page }) => {
    mockCart = [];
    loggedInUser = null;

    // Intercept auth/check API
    await page.route('**/api/auth/check', async (route) => {
      if (loggedInUser) {
        await route.fulfill({ status: 200, json: loggedInUser });
      } else {
        await route.fulfill({ status: 401, json: { message: 'Unauthorized' } });
      }
    });

    // Intercept login API
    await page.route('**/api/auth/login', async (route) => {
      loggedInUser = { id: 'u1', email: 'linhtran@gmail.com', full_name: 'Linh Tran' };
      await route.fulfill({ status: 200, json: loggedInUser });
    });

    // Intercept categories API
    await page.route('**/api/categories', async (route) => {
      await route.fulfill({
        status: 200,
        json: [{ id: 'c1', name: 'Vợt cầu lông' }],
      });
    });

    // Intercept products API
    await page.route('**/api/products', async (route) => {
      await route.fulfill({
        status: 200,
        json: [
          {
            id: 'p1',
            name: 'Vợt cầu lông Yonex Arcsaber',
            slug: 'vot-cau-long-yonex',
            price: 1500000,
            description: 'Vợt cầu lông chất lượng cao phù hợp cho mọi người chơi.',
            stockQuantity: 10,
            thumbnailImage: 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=400',
            category: { id: 'c1', name: 'Vợt cầu lông' }
          }
        ],
      });
    });

    await page.route('**/api/products/featured', async (route) => {
      await route.fulfill({ status: 200, json: [] });
    });

    // Intercept product detail API
    await page.route('**/api/products/detail/p1', async (route) => {
      await route.fulfill({
        status: 200,
        json: {
          id: 'p1',
          name: 'Vợt cầu lông Yonex Arcsaber',
          slug: 'vot-cau-long-yonex',
          price: 1500000,
          description: 'Vợt cầu lông chất lượng cao phù hợp cho mọi người chơi.',
          stockQuantity: 10,
          thumbnailImage: 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=400',
          category: { id: 'c1', name: 'Vợt cầu lông' }
        },
      });
    });

    // Intercept cart API
    await page.route('**/api/cart', async (route, request) => {
      const method = request.method();

      if (method === 'GET') {
        await route.fulfill({ status: 200, json: mockCart });
      } else if (method === 'POST') {
        if (!loggedInUser) {
          await route.fulfill({ status: 401, json: { message: 'Unauthorized' } });
          return;
        }
        const body = request.postDataJSON();
        const { productId, quantity } = body;

        if (productId === 'p1') {
          const stock = 10;
          const currentQtyInCart = mockCart.find(item => item.productId === productId)?.quantity || 0;
          if (currentQtyInCart + quantity > stock) {
            await route.fulfill({
              status: 400,
              json: { message: 'Insufficient inventory' }
            });
            return;
          }

          const existing = mockCart.find(item => item.productId === productId);
          if (existing) {
            existing.quantity += quantity;
            existing.subtotal = existing.quantity * existing.productPrice;
          } else {
            mockCart.push({
              productId: 'p1',
              productName: 'Vợt cầu lông Yonex Arcsaber',
              productPrice: 1500000,
              thumbnailImage: 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=400',
              quantity: quantity,
              subtotal: quantity * 1500000
            });
          }
          await route.fulfill({ status: 200, json: mockCart });
        } else {
          await route.fulfill({ status: 400, json: { message: 'Product not found' } });
        }
      } else if (method === 'PUT') {
        if (!loggedInUser) {
          await route.fulfill({ status: 401, json: { message: 'Unauthorized' } });
          return;
        }
        const body = request.postDataJSON();
        const { productId, quantity } = body;
        const stock = 10;
        if (quantity > stock) {
          await route.fulfill({
            status: 400,
            json: { message: 'Insufficient inventory' }
          });
          return;
        }

        const existing = mockCart.find(item => item.productId === productId);
        if (existing) {
          existing.quantity = quantity;
          existing.subtotal = quantity * existing.productPrice;
        }
        await route.fulfill({ status: 200, json: mockCart });
      } else if (method === 'DELETE') {
        if (!loggedInUser) {
          await route.fulfill({ status: 401, json: { message: 'Unauthorized' } });
          return;
        }
        const body = request.postDataJSON();
        const { productId } = body;
        mockCart = mockCart.filter(item => item.productId !== productId);
        await route.fulfill({ status: 200, json: mockCart });
      }
    });
  });

  test('TC1: Thêm sản phẩm vào giỏ hàng thành công', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const detailPage = new ProductDetailPage(page);

    // 1. Login
    await loginPage.goto();
    await loginPage.login('linhtran@gmail.com', '123456');

    // Check if modal closes or user session loads
    await expect(page.locator('[data-testid="open-login-modal-btn"]')).not.toBeVisible();

    // 2. Go to product detail
    await detailPage.gotoProduct('p1', 'vot-cau-long-yonex');

    // 3. Set quantity to 2 and add to cart
    await detailPage.setQuantity(2);
    await detailPage.addToCart();

    // 4. Verify Toast & Badge
    await expect(detailPage.successToast).toBeVisible();
    await expect(detailPage.successToast).toContainText('Thêm vào giỏ hàng thành công');
    await expect(detailPage.cartBadge).toHaveText('2');
  });

  test('TC2: Cảnh báo khi số lượng vượt quá tồn kho', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const detailPage = new ProductDetailPage(page);

    // 1. Login
    await loginPage.goto();
    await loginPage.login('linhtran@gmail.com', '123456');

    // 2. Go to product detail
    await detailPage.gotoProduct('p1', 'vot-cau-long-yonex');

    // 3. Set quantity to 11 (exceeding stock of 10)
    await detailPage.setQuantity(11);
    await detailPage.addToCart();

    // 4. Verify Error Toast
    await expect(detailPage.errorToast).toBeVisible();
    await expect(detailPage.errorToast).toContainText('Số lượng vượt quá tồn kho');

    // Verify badge is still empty/non-existent
    await expect(detailPage.cartBadge).not.toBeVisible();
  });

  test('TC3: Điều hướng giỏ hàng và tương tác tăng giảm, xóa sản phẩm', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const detailPage = new ProductDetailPage(page);
    const cartPage = new CartPage(page);

    // 1. Login
    await loginPage.goto();
    await loginPage.login('linhtran@gmail.com', '123456');

    // 2. Go to detail and add 2 items
    await detailPage.gotoProduct('p1', 'vot-cau-long-yonex');
    await detailPage.setQuantity(2);
    await detailPage.addToCart();
    await expect(detailPage.successToast).toBeVisible();

    // 3. Navigate to Cart
    await cartPage.goto();

    // Verify 1 item is listed
    await expect(cartPage.cartItems).toHaveCount(1);
    await expect(cartPage.cartItems.nth(0).locator('[data-testid="cart-item-qty"]')).toHaveText('2');
    await expect(cartPage.totalPrice).toHaveText('3.000.000₫');

    // 4. Increase quantity to 3
    await cartPage.increaseQuantity(0);
    await expect(cartPage.cartItems.nth(0).locator('[data-testid="cart-item-qty"]')).toHaveText('3');
    await expect(cartPage.totalPrice).toHaveText('4.500.000₫');

    // 5. Decrease quantity back to 2
    await cartPage.decreaseQuantity(0);
    await expect(cartPage.cartItems.nth(0).locator('[data-testid="cart-item-qty"]')).toHaveText('2');
    await expect(cartPage.totalPrice).toHaveText('3.000.000₫');

    // 6. Delete item from cart
    await cartPage.removeItem(0);

    // Verify cart is empty
    await expect(cartPage.cartItems).toHaveCount(0);
    await expect(page.locator('text=Giỏ hàng của bạn đang trống')).toBeVisible();
  });
});
