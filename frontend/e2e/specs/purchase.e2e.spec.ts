import { test, expect } from '@playwright/test';
import { LoginPage } from '../pages/LoginPage';
import { CheckoutPage } from '../pages/CheckoutPage';

let loggedInUser: any = null;
let mockCart: any[] = [];
let mockAddresses: any[] = [];
let mockShippingMethods: any[] = [];
let mockPaymentMethods: any[] = [];
let mockCoupons: any[] = [];

test.describe('Purchase E2E Tests (Kiểm thử đặt hàng)', () => {
  test.beforeEach(async ({ page }) => {
    // Reset data
    loggedInUser = null;
    mockCart = [
      {
        productId: 'p1',
        productName: 'Vợt cầu lông Yonex Arcsaber',
        productPrice: 1500000,
        thumbnailImage: 'https://images.unsplash.com/photo-1626224583764-f87db24ac4ea?w=400',
        quantity: 2,
        subtotal: 3000000,
      },
    ];

    mockAddresses = [
      {
        id: 'addr-01',
        addressLine: '123 Nguyễn Huệ',
        ward: 'Phường Bến Thành',
        city: 'Quận 1',
        district: 'TP. Hồ Chí Minh',
        isDefault: true,
      },
    ];

    mockShippingMethods = [
      {
        id: 'ship-01',
        name: 'Giao hàng nhanh',
        description: 'Nhận hàng sau 2-3 ngày',
        baseFee: 30000,
        estimatedDaysMin: 2,
        estimatedDaysMax: 3,
      },
    ];

    mockPaymentMethods = [
      { id: 'pay-01', name: 'Thanh toán khi nhận hàng (COD)' },
    ];

    mockCoupons = [
      {
        code: 'YONEX10',
        discountType: 'PERCENT',
        discountValue: 10,
        maxDiscount: 50000,
        minOrderValue: 500000,
        remainingUsage: 5,
        expiryDate: '2027-12-31',
        applicableToCurrentCart: true,
        scope: 'ORDER',
      },
    ];

    // Intercept APIs
    await page.route('**/api/auth/check', async (route) => {
      if (loggedInUser) {
        await route.fulfill({ status: 200, json: loggedInUser });
      } else {
        await route.fulfill({ status: 401, json: { message: 'Unauthorized' } });
      }
    });

    await page.route('**/api/auth/login', async (route) => {
      loggedInUser = { id: 'u1', email: 'linhtran@gmail.com', full_name: 'Linh Tran' };
      await route.fulfill({ status: 200, json: loggedInUser });
    });

    await page.route('**/api/cart', async (route) => {
      await route.fulfill({ status: 200, json: mockCart });
    });

    await page.route('**/api/address**', async (route) => {
      await route.fulfill({ status: 200, json: mockAddresses });
    });

    await page.route('**/api/shipping-methods', async (route) => {
      await route.fulfill({ status: 200, json: mockShippingMethods });
    });

    await page.route('**/api/payment-methods', async (route) => {
      await route.fulfill({ status: 200, json: mockPaymentMethods });
    });

    await page.route('**/api/coupons**', async (route) => {
      await route.fulfill({ status: 200, json: mockCoupons });
    });

    // Intercept checkout submit API
    await page.route('**/api/orders', async (route) => {
      await route.fulfill({
        status: 201,
        json: {
          id: 'ord-12345',
          createdAt: new Date().toISOString(),
          shippingInfo: {
            fullName: 'Linh Tran',
            phone: '0912345678',
            addressLine: '123 Nguyễn Huệ',
            ward: 'Phường Bến Thành',
            city: 'Quận 1',
            district: 'TP. Hồ Chí Minh',
            methodName: 'Giao hàng nhanh',
            estimatedDeliveryMin: 2,
            estimatedDeliveryMax: 3,
          },
          paymentInfo: {
            methodName: 'Thanh toán khi nhận hàng (COD)',
            status: 'PENDING',
          },
          items: [
            {
              productId: 'p1',
              productName: 'Vợt cầu lông Yonex Arcsaber',
              price: 1500000,
              quantity: 2,
              totalPrice: 3000000,
            },
          ],
          pricingInfo: {
            subtotal: 3000000,
            shippingFee: 30000,
            discount: 50000,
            finalPrice: 2980000,
          },
          appliedCoupons: [{ couponId: 'c1', code: 'YONEX10', discountAmount: 50000 }],
        },
      });
    });
  });

  // ── TC1: Đặt hàng thành công ──────────────────────────────────────────────
  test('TC1: Đặt hàng thành công hoàn chỉnh luồng (Complete Checkout Flow)', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const checkoutPage = new CheckoutPage(page);

    // 1. Đăng nhập hệ thống
    await loginPage.goto();
    await loginPage.login('linhtran@gmail.com', '123456');

    // 2. Đi tới trang thanh toán
    await checkoutPage.goToCheckout();

    // 3. Điền thông tin người nhận
    await checkoutPage.fillRecipientInfo('Linh Tran', '0912345678');

    // 4. Chọn các trường mặc định
    await checkoutPage.selectFirstAddress();
    await checkoutPage.selectFirstShipping();
    await checkoutPage.selectFirstPayment();

    // 5. Xác nhận nút đặt hàng và đi tới trang thành công
    await checkoutPage.placeOrder();

    // 6. Kiểm tra giao diện hiển thị đặt hàng thành công
    await expect(checkoutPage.successMessage).toContainText('Đặt hàng thành công!');
    await expect(page).toHaveURL(/\/order\/success/);
  });

  // ── TC2: Tính giá thanh toán chính xác ─────────────────────────────────────
  test('TC2: Tính giá thanh toán chính xác bao gồm tiền hàng, ship, coupon', async ({ page }) => {
    const loginPage = new LoginPage(page);
    const checkoutPage = new CheckoutPage(page);

    await loginPage.goto();
    await loginPage.login('linhtran@gmail.com', '123456');

    await checkoutPage.goToCheckout();

    // Kiểm tra tổng thanh toán (Mặc định chọn sẵn phương thức giao hàng đầu tiên: 3.030.000đ)
    let totalText = await checkoutPage.getTotalPrice();
    expect(totalText).toContain('3.030.000');

    // Áp dụng mã giảm giá YONEX10 (Giảm 10% tối đa 50k -> còn 2.980.000đ)
    await checkoutPage.selectCoupon('YONEX10');

    totalText = await checkoutPage.getTotalPrice();
    expect(totalText).toContain('2.980.000');
  });
});
