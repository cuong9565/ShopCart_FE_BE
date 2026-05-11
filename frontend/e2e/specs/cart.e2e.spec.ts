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

    // ================= AUTH =================
    await page.route('**/api/auth/check', async (route) => {
      if (loggedInUser) {
        await route.fulfill({
          status: 200,
          json: loggedInUser,
        });
      } else {
        await route.fulfill({
          status: 401,
          json: { message: 'Unauthorized' },
        });
      }
    });

    await page.route('**/api/auth/login', async (route) => {
      loggedInUser = {
        id: 'u1',
        email: 'linhtran@gmail.com',
        full_name: 'Linh Tran',
      };

      await route.fulfill({
        status: 200,
        json: loggedInUser,
      });
    });

    // ================= CATEGORY =================
    await page.route('**/api/categories', async (route) => {
      await route.fulfill({
        status: 200,
        json: [
          {
            id: 'c1',
            name: 'Vợt cầu lông',
          },
        ],
      });
    });

    // ================= PRODUCTS =================
    const productData = {
      id: 'p1',
      name: 'Vợt cầu lông Yonex Arcsaber',
      slug: 'vot-cau-long-yonex',
      price: 1500000,
      description:
        'Vợt cầu lông chất lượng cao phù hợp cho mọi người chơi.',
      stockQuantity: 10,
      thumbnailImage:
        'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=400',
      category: {
        id: 'c1',
        name: 'Vợt cầu lông',
      },
    };

    await page.route('**/api/products', async (route) => {
      await route.fulfill({
        status: 200,
        json: [productData],
      });
    });

    await page.route('**/api/products/featured', async (route) => {
      await route.fulfill({
        status: 200,
        json: [],
      });
    });

    await page.route('**/api/products/detail/p1', async (route) => {
      await route.fulfill({
        status: 200,
        json: productData,
      });
    });

    // ================= CART =================
    await page.route('**/api/cart', async (route, request) => {
      const method = request.method();

      // GET CART
      if (method === 'GET') {
        await route.fulfill({
          status: 200,
          json: mockCart,
        });

        return;
      }

      // CHECK LOGIN
      if (!loggedInUser) {
        await route.fulfill({
          status: 401,
          json: { message: 'Unauthorized' },
        });

        return;
      }

      // ADD TO CART
      if (method === 'POST') {
        const body = request.postDataJSON();

        const { productId, quantity } = body;

        const stock = 10;

        const currentQty =
          mockCart.find((item) => item.productId === productId)?.quantity || 0;

        if (currentQty + quantity > stock) {
          await route.fulfill({
            status: 400,
            json: {
              message: 'Chỉ còn 10 sản phẩm',
            },
          });

          return;
        }

        const existing = mockCart.find(
          (item) => item.productId === productId
        );

        if (existing) {
          existing.quantity += quantity;
          existing.subtotal =
            existing.quantity * existing.productPrice;
        } else {
          mockCart.push({
            productId: 'p1',
            productName: 'Vợt cầu lông Yonex Arcsaber',
            productPrice: 1500000,
            thumbnailImage: productData.thumbnailImage,
            quantity,
            subtotal: quantity * 1500000,
          });
        }

        await route.fulfill({
          status: 200,
          json: mockCart,
        });

        return;
      }

      // UPDATE QUANTITY
      if (method === 'PUT') {
        const body = request.postDataJSON();

        const { productId, quantity } = body;

        const stock = 10;

        if (quantity > stock) {
          await route.fulfill({
            status: 400,
            json: {
              message: 'Chỉ còn 10 sản phẩm',
            },
          });

          return;
        }

        const existing = mockCart.find(
          (item) => item.productId === productId
        );

        if (existing) {
          existing.quantity = quantity;
          existing.subtotal =
            existing.quantity * existing.productPrice;
        }

        await route.fulfill({
          status: 200,
          json: mockCart,
        });

        return;
      }

      // DELETE ITEM
      if (method === 'DELETE') {
        const body = request.postDataJSON();

        const { productId } = body;

        mockCart = mockCart.filter(
          (item) => item.productId !== productId
        );

        await route.fulfill({
          status: 200,
          json: mockCart,
        });

        return;
      }
    });
  });

  // =========================================================
  // TC1
  // =========================================================
  test('TC1: Thêm sản phẩm vào giỏ hàng thành công', async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const detailPage = new ProductDetailPage(page);

    await loginPage.goto();

    await loginPage.login(
      'linhtran@gmail.com',
      '123456'
    );

    await expect(
      page.locator('[data-testid="open-login-modal-btn"]')
    ).not.toBeVisible();

    await detailPage.gotoProduct(
      'p1',
      'vot-cau-long-yonex'
    );

    await detailPage.setQuantity(2);

    await detailPage.addToCart();

    await expect(detailPage.successToast).toBeVisible();

    await expect(detailPage.successToast)
      .toContainText('Thêm vào giỏ hàng thành công');

    await expect(detailPage.cartBadge)
      .toHaveText('2');
  });

  // =========================================================
  // TC2
  // =========================================================
  test('TC2: Cảnh báo khi số lượng vượt quá tồn kho', async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const detailPage = new ProductDetailPage(page);

    await loginPage.goto();

    await loginPage.login(
      'linhtran@gmail.com',
      '123456'
    );

    await detailPage.gotoProduct(
      'p1',
      'vot-cau-long-yonex'
    );

    // exceed stock
    await detailPage.setQuantity(11);

    await detailPage.addToCart();

    await expect(detailPage.errorToast)
      .toBeVisible();

    // flexible assertion
    await expect(detailPage.errorToast)
      .toContainText('Chỉ còn 10 sản phẩm');

    await expect(detailPage.cartBadge)
      .toHaveText('10');
  });

  // =========================================================
  // TC3
  // =========================================================
  test('TC3: Điều hướng giỏ hàng và tăng giảm xóa sản phẩm', async ({
    page,
  }) => {
    const loginPage = new LoginPage(page);
    const detailPage = new ProductDetailPage(page);
    const cartPage = new CartPage(page);

    // LOGIN
    await loginPage.goto();

    await loginPage.login(
      'linhtran@gmail.com',
      '123456'
    );

    // ADD PRODUCT
    await detailPage.gotoProduct(
      'p1',
      'vot-cau-long-yonex'
    );

    await detailPage.setQuantity(2);

    await detailPage.addToCart();

    // await expect(detailPage.successToast)
    //   .toBeVisible();
    await expect(detailPage.cartBadge)
      .toHaveText('2');

    // GO TO CART
    await cartPage.goto();

    await page.waitForLoadState('networkidle');

    // VERIFY ITEM
    await expect(cartPage.cartItems)
      .toHaveCount(1);

    await expect(
      cartPage.cartItems
        .nth(0)
        .locator('[data-testid="cart-item-qty"]')
    ).toHaveText('2');

    await expect(cartPage.totalPrice)
      .toHaveText('3.000.000đ');

    // INCREASE
    await cartPage.increaseQuantity(0);

    await expect(
      cartPage.cartItems
        .nth(0)
        .locator('[data-testid="cart-item-qty"]')
    ).toHaveText('3');

    await expect(cartPage.totalPrice)
      .toHaveText('4.500.000đ');

    // DECREASE
    await cartPage.decreaseQuantity(0);

    await expect(
      cartPage.cartItems
        .nth(0)
        .locator('[data-testid="cart-item-qty"]')
    ).toHaveText('2');

    await expect(cartPage.totalPrice)
      .toHaveText('3.000.000đ');

    // DELETE
    await cartPage.removeItem(0);

    // wait for rerender
    await page.waitForLoadState('networkidle');

    // VERIFY EMPTY
    await expect(cartPage.cartItems)
      .toHaveCount(0);

    // flexible empty check
    const emptyCartText = page.locator(
      'text=/giỏ hàng.*trống/i'
    );

    await expect(emptyCartText)
      .toBeVisible();
  });
});