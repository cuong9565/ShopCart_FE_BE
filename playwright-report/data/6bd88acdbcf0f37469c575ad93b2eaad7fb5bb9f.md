# Instructions

- Following Playwright test failed.
- Explain why, be concise, respect Playwright best practices.
- Provide a snippet of code with the fix, if possible.

# Test info

- Name: cart.spec.ts >> Cart E2E Tests >> Add to cart success
- Location: tests\cart.spec.ts:32:7

# Error details

```
Test timeout of 30000ms exceeded while running "beforeEach" hook.
```

```
Error: page.click: Test timeout of 30000ms exceeded.
Call log:
  - waiting for locator('[data-testid="login-open-btn"]')

```

# Page snapshot

```yaml
- generic [ref=e3]:
  - navigation [ref=e4]:
    - link "Logo" [ref=e6] [cursor=pointer]:
      - /url: /
      - img "Logo" [ref=e7]
    - button "Đăng nhập" [ref=e9] [cursor=pointer]
  - main [ref=e10]:
    - generic [ref=e11]:
      - generic [ref=e12]:
        - img "Slide 1" [ref=e14]
        - img "Slide 2" [ref=e17]
        - img "Slide 3" [ref=e20]
      - generic [ref=e22]:
        - button "Slide 1" [ref=e23]
        - button "Slide 2" [ref=e24]
        - button "Slide 3" [ref=e25]
      - button "Previous" [ref=e26] [cursor=pointer]:
        - generic [ref=e27]:
          - img [ref=e28]
          - generic [ref=e30]: Previous
      - button "Next" [ref=e31] [cursor=pointer]:
        - generic [ref=e32]:
          - img [ref=e33]
          - generic [ref=e35]: Next
    - generic [ref=e37]:
      - list [ref=e40]:
        - listitem [ref=e41]:
          - button "Tất cả sản phẩm" [ref=e42] [cursor=pointer]
        - listitem [ref=e43]:
          - button "Vợt cầu lông Yonex" [ref=e44] [cursor=pointer]
        - listitem [ref=e45]:
          - button "Vợt cầu lông Victor" [ref=e46] [cursor=pointer]
        - listitem [ref=e47]:
          - button "Vợt cầu lông Lining" [ref=e48] [cursor=pointer]
        - listitem [ref=e49]:
          - button "Vợt cầu lông VS" [ref=e50] [cursor=pointer]
        - listitem [ref=e51]:
          - button "Vợt cầu lông Mizuno" [ref=e52] [cursor=pointer]
      - generic [ref=e53]:
        - generic [ref=e54]:
          - heading "Sản phẩm nổi bật" [level=2] [ref=e56]
          - generic [ref=e57]:
            - link "Vợt cầu lông Lining Bladex Assassin Vợt cầu lông Lining Bladex Assassin 1.300.000đ" [ref=e58] [cursor=pointer]:
              - /url: /product/vot-cau-long-lining-bladex-assassin/4569823e-8674-477d-9f02-ce0ddb60dcb1
              - img "Vợt cầu lông Lining Bladex Assassin" [ref=e60]
              - generic [ref=e61]:
                - heading "Vợt cầu lông Lining Bladex Assassin" [level=3] [ref=e62]
                - generic [ref=e63]:
                  - generic [ref=e64]: 1.300.000đ
                  - button [ref=e65]:
                    - img [ref=e66]
            - link "Vợt cầu lông Yonex Astrox 100 Tour VA Vợt cầu lông Yonex Astrox 100 Tour VA 4.469.000đ" [ref=e68] [cursor=pointer]:
              - /url: /product/vot-cau-long-yonex-astrox-100-tour-va/486ba275-8cc1-4c5f-a5c8-dcbaefdc86bc
              - img "Vợt cầu lông Yonex Astrox 100 Tour VA" [ref=e70]
              - generic [ref=e71]:
                - heading "Vợt cầu lông Yonex Astrox 100 Tour VA" [level=3] [ref=e72]
                - generic [ref=e73]:
                  - generic [ref=e74]: 4.469.000đ
                  - button [ref=e75]:
                    - img [ref=e76]
            - link "Vợt Cầu Lông Mizuno Acrospeed 8 Vợt Cầu Lông Mizuno Acrospeed 8 3.150.000đ" [ref=e78] [cursor=pointer]:
              - /url: /product/vot-cau-long-mizuno-acrospeed-8/d4992f68-5841-4545-9512-e6cff4dada38
              - img "Vợt Cầu Lông Mizuno Acrospeed 8" [ref=e80]
              - generic [ref=e81]:
                - heading "Vợt Cầu Lông Mizuno Acrospeed 8" [level=3] [ref=e82]
                - generic [ref=e83]:
                  - generic [ref=e84]: 3.150.000đ
                  - button [ref=e85]:
                    - img [ref=e86]
            - link "Vợt cầu lông Mizuno XYST 07 Vợt cầu lông Mizuno XYST 07 3.033.000đ" [ref=e88] [cursor=pointer]:
              - /url: /product/vot-cau-long-mizuno-xyst-07/08f10b51-f4b1-469c-b43d-e26ffa69ca9b
              - img "Vợt cầu lông Mizuno XYST 07" [ref=e90]
              - generic [ref=e91]:
                - heading "Vợt cầu lông Mizuno XYST 07" [level=3] [ref=e92]
                - generic [ref=e93]:
                  - generic [ref=e94]: 3.033.000đ
                  - button [ref=e95]:
                    - img [ref=e96]
            - link "Vợt cầu lông Yonex Astrox 22 Lite (BK/RD) chính hãng Vợt cầu lông Yonex Astrox 22 Lite (BK/RD) chính hãng 2.349.000đ" [ref=e98] [cursor=pointer]:
              - /url: /product/vot-cau-long-yonex-astrox-22-lite-bk-rd/1921ef85-bfca-4fb4-a496-57ff4a201ddb
              - img "Vợt cầu lông Yonex Astrox 22 Lite (BK/RD) chính hãng" [ref=e100]
              - generic [ref=e101]:
                - heading "Vợt cầu lông Yonex Astrox 22 Lite (BK/RD) chính hãng" [level=3] [ref=e102]
                - generic [ref=e103]:
                  - generic [ref=e104]: 2.349.000đ
                  - button [ref=e105]:
                    - img [ref=e106]
            - link "Vợt cầu Lông Victor Thruster Ryuga Muse F chính hãng Vợt cầu Lông Victor Thruster Ryuga Muse F chính hãng 3.300.000đ" [ref=e108] [cursor=pointer]:
              - /url: /product/vot-cau-long-victor-thruster-ryuga-muse-f/bf3c3ca4-58f0-4ffe-8334-16f8fee52d7c
              - img "Vợt cầu Lông Victor Thruster Ryuga Muse F chính hãng" [ref=e110]
              - generic [ref=e111]:
                - heading "Vợt cầu Lông Victor Thruster Ryuga Muse F chính hãng" [level=3] [ref=e112]
                - generic [ref=e113]:
                  - generic [ref=e114]: 3.300.000đ
                  - button [ref=e115]:
                    - img [ref=e116]
        - generic [ref=e118]:
          - heading "Tất cả sản phẩm" [level=2] [ref=e120]
          - generic [ref=e121]:
            - link "Vợt cầu lông Lining Bladex Assassin Vợt cầu lông Lining Bladex Assassin 1.300.000đ" [ref=e122] [cursor=pointer]:
              - /url: /product/vot-cau-long-lining-bladex-assassin/4569823e-8674-477d-9f02-ce0ddb60dcb1
              - img "Vợt cầu lông Lining Bladex Assassin" [ref=e124]
              - generic [ref=e125]:
                - heading "Vợt cầu lông Lining Bladex Assassin" [level=3] [ref=e126]
                - generic [ref=e127]:
                  - generic [ref=e128]: 1.300.000đ
                  - button [ref=e129]:
                    - img [ref=e130]
            - link "Vợt cầu lông Yonex Astrox 100 Tour VA Vợt cầu lông Yonex Astrox 100 Tour VA 4.469.000đ" [ref=e132] [cursor=pointer]:
              - /url: /product/vot-cau-long-yonex-astrox-100-tour-va/486ba275-8cc1-4c5f-a5c8-dcbaefdc86bc
              - img "Vợt cầu lông Yonex Astrox 100 Tour VA" [ref=e134]
              - generic [ref=e135]:
                - heading "Vợt cầu lông Yonex Astrox 100 Tour VA" [level=3] [ref=e136]
                - generic [ref=e137]:
                  - generic [ref=e138]: 4.469.000đ
                  - button [ref=e139]:
                    - img [ref=e140]
            - link "Vợt cầu lông Victor ARS 30H Vợt cầu lông Victor ARS 30H 1.250.000đ" [ref=e142] [cursor=pointer]:
              - /url: /product/vot-cau-long-victor-ars-30h/c769b693-31d6-4411-b9ee-60b0adface9a
              - img "Vợt cầu lông Victor ARS 30H" [ref=e144]
              - generic [ref=e145]:
                - heading "Vợt cầu lông Victor ARS 30H" [level=3] [ref=e146]
                - generic [ref=e147]:
                  - generic [ref=e148]: 1.250.000đ
                  - button [ref=e149]:
                    - img [ref=e150]
            - link "Set Vợt cầu lông VS Energetic Long Mã Chính Hãng Set Vợt cầu lông VS Energetic Long Mã Chính Hãng 1.850.000đ" [ref=e152] [cursor=pointer]:
              - /url: /product/set-vot-cau-long-vs-energetic-long-ma/3e506f92-eb9c-49ec-9f0a-9588ae7dee33
              - img "Set Vợt cầu lông VS Energetic Long Mã Chính Hãng" [ref=e154]
              - generic [ref=e155]:
                - heading "Set Vợt cầu lông VS Energetic Long Mã Chính Hãng" [level=3] [ref=e156]
                - generic [ref=e157]:
                  - generic [ref=e158]: 1.850.000đ
                  - button [ref=e159]:
                    - img [ref=e160]
            - link "Vợt cầu lông VS Goddess 8 Chính Hãng Vợt cầu lông VS Goddess 8 Chính Hãng 1.350.000đ" [ref=e162] [cursor=pointer]:
              - /url: /product/vot-cau-long-vs-goddess-8/53f30f99-6e13-4fcc-b7bc-1dd15e39ac94
              - img "Vợt cầu lông VS Goddess 8 Chính Hãng" [ref=e164]
              - generic [ref=e165]:
                - heading "Vợt cầu lông VS Goddess 8 Chính Hãng" [level=3] [ref=e166]
                - generic [ref=e167]:
                  - generic [ref=e168]: 1.350.000đ
                  - button [ref=e169]:
                    - img [ref=e170]
            - link "Vợt Cầu Lông Mizuno Acrospeed 8 Vợt Cầu Lông Mizuno Acrospeed 8 3.150.000đ" [ref=e172] [cursor=pointer]:
              - /url: /product/vot-cau-long-mizuno-acrospeed-8/d4992f68-5841-4545-9512-e6cff4dada38
              - img "Vợt Cầu Lông Mizuno Acrospeed 8" [ref=e174]
              - generic [ref=e175]:
                - heading "Vợt Cầu Lông Mizuno Acrospeed 8" [level=3] [ref=e176]
                - generic [ref=e177]:
                  - generic [ref=e178]: 3.150.000đ
                  - button [ref=e179]:
                    - img [ref=e180]
            - link "Vợt cầu lông Mizuno XYST 07 Vợt cầu lông Mizuno XYST 07 3.033.000đ" [ref=e182] [cursor=pointer]:
              - /url: /product/vot-cau-long-mizuno-xyst-07/08f10b51-f4b1-469c-b43d-e26ffa69ca9b
              - img "Vợt cầu lông Mizuno XYST 07" [ref=e184]
              - generic [ref=e185]:
                - heading "Vợt cầu lông Mizuno XYST 07" [level=3] [ref=e186]
                - generic [ref=e187]:
                  - generic [ref=e188]: 3.033.000đ
                  - button [ref=e189]:
                    - img [ref=e190]
            - link "Vợt cầu lông Yonex Astrox 22 Lite (BK/RD) chính hãng Vợt cầu lông Yonex Astrox 22 Lite (BK/RD) chính hãng 2.349.000đ" [ref=e192] [cursor=pointer]:
              - /url: /product/vot-cau-long-yonex-astrox-22-lite-bk-rd/1921ef85-bfca-4fb4-a496-57ff4a201ddb
              - img "Vợt cầu lông Yonex Astrox 22 Lite (BK/RD) chính hãng" [ref=e194]
              - generic [ref=e195]:
                - heading "Vợt cầu lông Yonex Astrox 22 Lite (BK/RD) chính hãng" [level=3] [ref=e196]
                - generic [ref=e197]:
                  - generic [ref=e198]: 2.349.000đ
                  - button [ref=e199]:
                    - img [ref=e200]
            - link "Vợt cầu lông Lining Halbertec 7000 - Purple twilight chính hãng Vợt cầu lông Lining Halbertec 7000 - Purple twilight chính hãng 3.990.000đ" [ref=e202] [cursor=pointer]:
              - /url: /product/vot-cau-long-lining-halbertec-7000-purple-twilight/4f268170-c0d6-4edb-a617-c1372e864291
              - img "Vợt cầu lông Lining Halbertec 7000 - Purple twilight chính hãng" [ref=e204]
              - generic [ref=e205]:
                - heading "Vợt cầu lông Lining Halbertec 7000 - Purple twilight chính hãng" [level=3] [ref=e206]
                - generic [ref=e207]:
                  - generic [ref=e208]: 3.990.000đ
                  - button [ref=e209]:
                    - img [ref=e210]
            - link "Vợt cầu Lông Victor Thruster Ryuga Muse F chính hãng Vợt cầu Lông Victor Thruster Ryuga Muse F chính hãng 3.300.000đ" [ref=e212] [cursor=pointer]:
              - /url: /product/vot-cau-long-victor-thruster-ryuga-muse-f/bf3c3ca4-58f0-4ffe-8334-16f8fee52d7c
              - img "Vợt cầu Lông Victor Thruster Ryuga Muse F chính hãng" [ref=e214]
              - generic [ref=e215]:
                - heading "Vợt cầu Lông Victor Thruster Ryuga Muse F chính hãng" [level=3] [ref=e216]
                - generic [ref=e217]:
                  - generic [ref=e218]: 3.300.000đ
                  - button [ref=e219]:
                    - img [ref=e220]
  - contentinfo [ref=e222]:
    - generic [ref=e223]:
      - generic [ref=e224]:
        - paragraph [ref=e225]: DECATHLON
        - paragraph [ref=e226]: Chuyên cung cấp dụng cụ thể thao cao cấp, đặc biệt là Pickleball. Cam kết chất lượng và trải nghiệm tốt nhất cho vận động viên.
        - generic [ref=e227]:
          - link [ref=e228] [cursor=pointer]:
            - /url: "#"
            - img [ref=e229]
          - link [ref=e231] [cursor=pointer]:
            - /url: "#"
            - img [ref=e232]
          - link [ref=e234] [cursor=pointer]:
            - /url: "#"
            - img [ref=e235]
          - link [ref=e237] [cursor=pointer]:
            - /url: "#"
            - img [ref=e238]
      - generic [ref=e240]:
        - heading "Liên kết nhanh" [level=4] [ref=e241]
        - list [ref=e242]:
          - listitem [ref=e243]:
            - link "Trang chủ" [ref=e244] [cursor=pointer]:
              - /url: "#"
          - listitem [ref=e245]:
            - link "Sản phẩm" [ref=e246] [cursor=pointer]:
              - /url: "#"
          - listitem [ref=e247]:
            - link "Về chúng tôi" [ref=e248] [cursor=pointer]:
              - /url: "#"
          - listitem [ref=e249]:
            - link "Tin tức" [ref=e250] [cursor=pointer]:
              - /url: "#"
          - listitem [ref=e251]:
            - link "Liên hệ" [ref=e252] [cursor=pointer]:
              - /url: "#"
      - generic [ref=e253]:
        - heading "Hỗ trợ khách hàng" [level=4] [ref=e254]
        - list [ref=e255]:
          - listitem [ref=e256]:
            - link "Chính sách bảo hành" [ref=e257] [cursor=pointer]:
              - /url: "#"
          - listitem [ref=e258]:
            - link "Chính sách đổi trả" [ref=e259] [cursor=pointer]:
              - /url: "#"
          - listitem [ref=e260]:
            - link "Phương thức thanh toán" [ref=e261] [cursor=pointer]:
              - /url: "#"
          - listitem [ref=e262]:
            - link "Vận chuyển & Giao hàng" [ref=e263] [cursor=pointer]:
              - /url: "#"
      - generic [ref=e264]:
        - heading "Thông tin liên hệ" [level=4] [ref=e265]
        - list [ref=e266]:
          - listitem [ref=e267]:
            - img [ref=e268]
            - generic [ref=e270]: 123 Đường Thể Thao, Quận 1, TP. Hồ Chí Minh
          - listitem [ref=e271]:
            - img [ref=e272]
            - generic [ref=e274]: +84 123 456 789
          - listitem [ref=e275]:
            - img [ref=e276]
            - generic [ref=e278]: support@shopcart.vn
    - paragraph [ref=e280]: © 2024 ShopCart Sports. All rights reserved. Designed for athletes.
```

# Test source

```ts
  1   | import { test, expect } from '@playwright/test';
  2   | 
  3   | test.describe('Cart E2E Tests', () => {
  4   | 
  5   |   test.beforeEach(async ({ page }) => {
  6   |     await page.goto('http://localhost:5173');
  7   | 
  8   |     // =========================
  9   |     // 1. OPEN LOGIN MODAL
  10  |     // =========================
> 11  |     await page.click('[data-testid="login-open-btn"]');
      |                ^ Error: page.click: Test timeout of 30000ms exceeded.
  12  | 
  13  |     // =========================
  14  |     // 2. LOGIN
  15  |     // =========================
  16  |     await expect(page.locator('[data-testid="email-input"]')).toBeVisible();
  17  | 
  18  |     await page.fill('[data-testid="email-input"]', 'linhtran@gmail.com');
  19  |     await page.fill('[data-testid="password-input"]', '123456');
  20  | 
  21  |     await page.click('[data-testid="login-btn"]');
  22  | 
  23  |     // =========================
  24  |     // 3. WAIT APP READY (KHÔNG WAIT URL)
  25  |     // =========================
  26  |     await expect(page.locator('[data-testid="product-list"]')).toBeVisible();
  27  |   });
  28  | 
  29  |   // =========================
  30  |   // 1. ADD TO CART
  31  |   // =========================
  32  |   test('Add to cart success', async ({ page }) => {
  33  | 
  34  |     await page.click('[data-testid="add-to-cart-btn-1"]');
  35  | 
  36  |     await expect(page.locator('[data-testid="cart-badge"]'))
  37  |       .toHaveText('1');
  38  |   });
  39  | 
  40  |   // =========================
  41  |   // 2. UPDATE QUANTITY
  42  |   // =========================
  43  |   test('Update quantity in cart', async ({ page }) => {
  44  | 
  45  |     await page.goto('http://localhost:5173/cart');
  46  | 
  47  |     await page.click('[data-testid="increase-btn-1"]');
  48  | 
  49  |     await expect(page.locator('[data-testid="cart-quantity-1"]'))
  50  |       .toHaveText('2');
  51  |   });
  52  | 
  53  |   // =========================
  54  |   // 3. DECREASE QUANTITY
  55  |   // =========================
  56  |   test('Decrease quantity in cart', async ({ page }) => {
  57  | 
  58  |     await page.goto('http://localhost:5173/cart');
  59  | 
  60  |     await page.click('[data-testid="decrease-btn-1"]');
  61  | 
  62  |     await expect(page.locator('[data-testid="cart-quantity-1"]'))
  63  |       .toHaveText('1');
  64  |   });
  65  | 
  66  |   // =========================
  67  |   // 4. REMOVE ITEM
  68  |   // =========================
  69  |   test('Remove item from cart', async ({ page }) => {
  70  | 
  71  |     await page.goto('http://localhost:5173/cart');
  72  | 
  73  |     await page.click('[data-testid="remove-item-1"]');
  74  | 
  75  |     await expect(page.locator('[data-testid="cart-item-1"]'))
  76  |       .toHaveCount(0);
  77  |   });
  78  | 
  79  |   // =========================
  80  |   // 5. TOTAL PRICE
  81  |   // =========================
  82  |   test('Cart total updates correctly', async ({ page }) => {
  83  | 
  84  |     await page.goto('http://localhost:5173/cart');
  85  | 
  86  |     await expect(page.locator('[data-testid="cart-total"]'))
  87  |       .toBeVisible();
  88  | 
  89  |     await page.click('[data-testid="increase-btn-1"]');
  90  | 
  91  |     const total = await page.locator('[data-testid="cart-total"]').textContent();
  92  |     expect(total).not.toBeNull();
  93  |   });
  94  | 
  95  |   // =========================
  96  |   // 6. CHECKOUT
  97  |   // =========================
  98  |   test('Checkout button works', async ({ page }) => {
  99  | 
  100 |     await page.goto('http://localhost:5173/cart');
  101 | 
  102 |     await page.click('[data-testid="checkout-btn"]');
  103 | 
  104 |     await expect(page).toHaveURL(/checkout|order|success/);
  105 |   });
  106 | 
  107 | });
```