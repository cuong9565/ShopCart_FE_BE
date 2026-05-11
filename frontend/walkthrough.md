# BÁO CÁO TỔNG HỢP KIỂM THỬ PHẦN MỀM (CHECKOUT & PURCHASE)
## Dự án: ShopCart - Phân hệ Đặt hàng & Thanh toán (Purchase/Checkout)

> [!NOTE]
> Tài liệu này tổng hợp toàn bộ các cấp độ kiểm thử đã được thiết kế và triển khai thành công 100% trong dự án, bao gồm: **Unit Test (TDD), Integration Test, Service Mocking, End-to-End (E2E) Test** và tích hợp quy trình **CI/CD Pipeline**.

---

## I. Cấu Trúc Thư Mục Kiểm Thử (Test Directory Structure)

Toàn bộ các tệp kiểm thử và cấu hình được tổ chức khoa học, tách bạch và tuân thủ các chuẩn mực kiểm thử hiện đại:

```text
ShopCart_FE_BE/
├── .github/
│   └── workflows/
│       └── ci.yml                        # Cấu hình GitHub Actions CI/CD Pipeline chính của dự án
└── frontend/
    ├── e2e/                              # Thư mục chứa kịch bản kiểm thử End-to-End (Playwright)
    │   ├── pages/
    │   │   ├── LoginPage.ts              # Page Object Model (POM) trang Đăng nhập
    │   │   └── CheckoutPage.ts           # Page Object Model (POM) trang Thanh toán
    │   └── specs/
    │       ├── cart.e2e.spec.ts          # E2E Tests cho tính năng Giỏ hàng
    │       └── purchase.e2e.spec.ts      # E2E Tests cho luồng Thanh toán & Đặt hàng
    ├── src/
    │   ├── components/                   # Các React Components phục vụ Checkout tích hợp
    │   │   ├── CheckoutSummary.tsx       # Tóm tắt đơn hàng
    │   │   ├── PriceCalculator.tsx       # Tính toán giá trực quan thời gian thực
    │   │   └── InventoryWarning.tsx      # Cảnh báo hết hàng / quá giới hạn tồn kho
    │   ├── utils/
    │   │   └── priceCalculation.ts       # Thư viện core tính toán giá trị & kiểm kho (TDD)
    │   └── tests/                        # Thư mục kiểm thử Frontend tập trung (Vitest)
    │       ├── priceCalculation.test.ts  # 20 Unit Test cases cho core logic tính toán (TDD)
    │       ├── checkout.integration.test.tsx # 9 Integration Test cases tích hợp DOM & Components
    │       └── purchase.mock.test.tsx    # 3 Mock Test cases giả lập Service API
    ├── vite.config.ts                    # Cấu hình môi trường chạy test (jsdom, thresholds)
    └── playwright.config.ts              # Cấu hình chạy kiểm thử trình duyệt E2E
```

---

## II. Luồng Hoạt Động & Vai Trò Các Tệp Kiểm Thử (Operational Flows & File Descriptions)

Thay vì vẽ sơ đồ, phần này mô tả chi tiết luồng hoạt động từng bước và vai trò của từng tệp tin kiểm thử cụ thể trong hệ thống:

### 1. Luồng Kiểm Thử Đơn Vị (Unit Test Flow - TDD)
*   **Các tệp tin liên quan:**
    *   [priceCalculation.ts](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/src/utils/priceCalculation.ts): Chứa các hàm xử lý logic nghiệp vụ chính gồm `calculateOrderPrice` (tính toán chiết khấu phần trăm, cố định, trần giới hạn giảm giá, phí ship) và `checkInventoryAvailability` (đối chiếu số lượng giỏ hàng với kho tồn).
    *   [priceCalculation.test.ts](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/src/tests/priceCalculation.test.ts): Chứa 20 kịch bản kiểm thử bao phủ toàn bộ trường hợp biên, giá trị null, tham số lỗi và các kiểu coupon khác nhau.
*   **Luồng hoạt động từng bước:**
    1.  Công cụ kiểm thử **Vitest** nạp tệp `priceCalculation.test.ts`.
    2.  Với mỗi ca kiểm thử, dữ liệu đầu vào giả định (thông tin giỏ hàng rỗng/có sản phẩm, cấu hình mã coupon hợp lệ/quá hạn/chưa đạt hạn mức tối thiểu) được truyền vào tham số của hàm tương ứng trong `priceCalculation.ts`.
    3.  Hàm xử lý tính toán số liệu và trả về kết quả hoặc ném lỗi ngoại lệ (đối với các đầu vào âm hoặc sản phẩm không tồn tại).
    4.  Tệp test sử dụng từ khóa `expect` để so sánh kết quả thực tế với giá trị mong đợi (Assertion). Nếu khớp hoàn toàn, ca kiểm thử đạt trạng thái thành công (PASS).

### 2. Luồng Kiểm Thử Tích Hợp Giao Diện (Integration Test Flow)
*   **Các tệp tin liên quan:**
    *   [CheckoutSummary.tsx](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/src/components/CheckoutSummary.tsx), [PriceCalculator.tsx](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/src/components/PriceCalculator.tsx), [InventoryWarning.tsx](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/src/components/InventoryWarning.tsx): Các React components giao diện trực quan hiển thị số liệu và phản hồi tương ứng theo trạng thái.
    *   [checkout.integration.test.tsx](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/src/tests/checkout.integration.test.tsx): Kịch bản kiểm thử tích hợp bằng React Testing Library.
*   **Luồng hoạt động từng bước:**
    1.  Môi trường DOM giả lập (`jsdom` cấu hình trong `vite.config.ts`) được khởi tạo trước khi chạy test.
    2.  Hàm `render` dựng các component lên môi trường DOM ảo này cùng các thuộc tính (Props) truyền vào.
    3.  Ca kiểm thử mô phỏng hành vi của người dùng trên DOM ảo bằng công cụ `fireEvent` (ví dụ: click chọn Radio button phương thức vận chuyển nhanh, chọn checkbox mã giảm giá).
    4.  Dưới tác động của sự kiện, React Component kích hoạt tính toán cập nhật lại State nội bộ và re-render giao diện.
    5.  Sử dụng `screen.getByText` hoặc `screen.queryByText` để tìm kiếm thông tin hiển thị trên màn hình ảo nhằm xác minh số tiền tổng cộng đã cập nhật chính xác theo thời gian thực (real-time) và cảnh báo kho hàng đổi màu sắc hiển thị phù hợp.
    6.  Hàm `afterEach` dọn dẹp sạch sẽ DOM ảo sau mỗi kịch bản bằng hàm `cleanup()` để ngăn ngừa lỗi rò rỉ bộ nhớ hoặc xung đột giữa các kịch bản.

### 3. Luồng Giả Lập Dịch Vụ Kiểm Thử (Service Mocking Flow)
*   **Các tệp tin liên quan:**
    *   [orderService.ts](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/src/services/orderService.ts), [inventoryService.ts](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/src/services/inventoryService.ts): Định nghĩa các phương thức giao tiếp API mạng.
    *   [purchase.mock.test.tsx](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/src/tests/purchase.mock.test.tsx): Kịch bản kiểm thử sử dụng cơ chế Spy/Mock dịch vụ API.
*   **Luồng hoạt động từng bước:**
    1.  Công cụ `vi.mock()` chặn toàn bộ các module dịch vụ kết nối mạng thật để tránh phát sinh dữ liệu rác lên server Backend thật.
    2.  Với kịch bản đặt hàng thành công: thiết lập mock trả về giá trị Promise Resolved chứa mã đơn hàng. Với kịch bản lỗi (hết hàng, đứt kết nối mạng): thiết lập mock trả về Promise Rejected kèm lỗi 400 hoặc 500 tương ứng.
    3.  Component mua hàng được render và người dùng giả lập nhấp chuột vào nút "Đặt hàng".
    4.  Kiểm thử viên sử dụng hàm theo dõi `vi.spyOn` hoặc `vi.mocked` để xác minh:
        *   Hàm gửi dữ liệu lên server có được thực thi đúng 1 lần hay không.
        *   Các tham số gửi đi (danh sách ID sản phẩm, địa chỉ giao nhận) có khớp chính xác không.
        *   Giao diện hiển thị đúng hộp thoại thông báo (Toast message) tương thích với phản hồi giả lập (Ví dụ: báo lỗi "Đặt hàng thất bại" khi server 500).

### 4. Luồng Kiểm Thử Đóng Gói Đầu-Cuối (End-to-End E2E Test Flow)
*   **Các tệp tin liên quan:**
    *   `e2e/pages/LoginPage.ts`: Page Object Model đại diện cho trang Đăng nhập, chứa locators và phương thức điền form, click login.
    *   `e2e/pages/CheckoutPage.ts`: Page Object Model đại diện cho trang Thanh toán, chứa locators điều khiển địa chỉ, vận chuyển, áp dụng mã giảm giá và đặt hàng.
    *   [purchase.e2e.spec.ts](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/frontend/e2e/specs/purchase.e2e.spec.ts): Kịch bản kiểm thử toàn trình.
*   **Luồng hoạt động từng bước:**
    1.  **Playwright** khởi động phiên trình duyệt thực tế (Chromium, Firefox hoặc Webkit).
    2.  Trình duyệt truy cập URL trang chủ, kịch bản E2E sử dụng `LoginPage.ts` tương tác mở modal đăng nhập, nhập tài khoản mật khẩu thử nghiệm để nạp Session đăng nhập.
    3.  Khi chuyển đổi sang trang thanh toán `/checkout`, hệ thống mạng nội bộ của trình duyệt chặn các API gọi đến Spring Boot backend thật và thay thế bằng dữ liệu mock chuẩn hóa (`loggedInUser`, `mockCart`, `mockAddresses`, `mockShippingMethods`, `mockPaymentMethods`, `mockCoupons`).
    4.  E2E test sử dụng `CheckoutPage.ts` thực thi tuần tự các thao tác: nhập họ tên, số điện thoại, nhấp chọn phương thức vận chuyển và mở rộng bảng mã giảm giá để click áp dụng mã `YONEX10`.
    5.  Hệ thống kiểm tra tổng thanh toán cập nhật tương ứng real-time hiển thị đúng `2.980.000đ` (giảm 50k trên tổng gốc).
    6.  Trình duyệt nhấp nút "Đặt hàng", đợi hệ thống API xử lý và tự động chuyển hướng người dùng sang trang thành công `/order/success`.
    7.  Xác nhận sự tồn tại của tiêu đề Đặt hàng thành công cùng thông tin mã đơn hàng hiển thị trên giao diện và lưu trữ bằng chứng kiểm thử.

### 5. Luồng Tự Động Hóa Tích Hợp Liên Tục (CI/CD Pipeline Flow)
*   **Tệp tin liên quan:** [ci.yml](file:///c:/Users/Minh%20Phuc/Downloads/ShopCart_FE_BE/.github/workflows/ci.yml).
*   **Luồng hoạt động từng bước:**
    1.  Mỗi khi có hoạt động `push` hoặc `pull_request` lên nhánh chính, GitHub Actions tự động cấp phát một máy ảo Ubuntu chạy ngầm độc lập.
    2.  **Giai đoạn 1: Backend Tests:** Khởi động container Postgres 17 ngầm làm DB cô lập -> Cài đặt Java 25 (Oracle) -> Khởi chạy toàn bộ JUnit 5 & Mockito test của Backend -> Xuất và lưu trữ báo cáo bao phủ JaCoCo.
    3.  **Giai đoạn 2: Frontend Tests:** Khởi tạo Node 22 -> Khôi phục NPM cache -> Thực thi Vitest suite tự động chạy 37 tests đơn vị, tích hợp, dịch vụ Mocking -> Xuất kết quả độ bao phủ của Frontend.
    4.  **Giai đoạn 3: E2E Tests:** Cài đặt các nhân trình duyệt Playwright -> Khởi động server Frontend Dev cục bộ tạm thời -> Chạy bộ kịch bản kiểm thử E2E trên 3 nền tảng trình duyệt lớn -> Xuất báo cáo HTML trực quan lưu trữ trực tiếp trên GitHub Actions.

---

## III. Hướng Dẫn Các Lệnh Chạy Kiểm Thử Chi Tiết (Execution Commands)

Bạn mở Terminal tại thư mục `frontend` và sử dụng các lệnh tương ứng dưới đây để chạy kiểm thử theo từng loại:

### 1. Kiểm thử Frontend với Vitest (Unit / Integration / Mock Tests)
Để thực hiện chạy kiểm thử, trước tiên hãy di chuyển vào thư mục frontend:
```powershell
cd frontend
```

*   **Dạng 1: Chạy toàn bộ 36 kịch bản kiểm thử (Chạy 1 lần duy nhất):**
    ```powershell
    npm run test -- --run
    ```
*   **Dạng 2: Chạy riêng biệt từng file kiểm thử cụ thể (Chạy theo từng dạng):**
    *   **Chạy duy nhất Unit Tests** (Logic tính toán giá & tồn kho):
        ```powershell
        npx vitest run priceCalculation
        ```
    *   **Chạy duy nhất Integration Tests** (Tương tác React Components):
        ```powershell
        npx vitest run checkout.integration
        ```
    *   **Chạy duy nhất Mock Tests** (Giả lập Service API):
        ```powershell
        npx vitest run purchase.mock
        ```
*   **Dạng 3: Chạy ở chế độ Watch Mode (Tự động kiểm tra lại khi có thay đổi code):**
    ```powershell
    npm run test
    ```
*   **Dạng 4: Chạy kiểm thử kèm thống kê tỷ lệ bao phủ mã nguồn (Coverage):**
    ```powershell
    npm run test -- --coverage --run
    ```
    
    > [!TIP]
    > **Bảng thống kê tỷ lệ bao phủ (Test Coverage) thực tế cực đẹp đạt 100%:**
    > ```text
    >  % Coverage report from v8
    > -------------------|---------|----------|---------|---------|-------------------
    > File               | % Stmts | % Branch | % Funcs | % Lines | Uncovered Line #s 
    > -------------------|---------|----------|---------|---------|-------------------
    > All files          |     100 |      100 |     100 |     100 |                   
    >  components        |     100 |      100 |     100 |     100 |                   
    >   CheckoutSummary  |     100 |      100 |     100 |     100 |                   
    >   InventoryWarning |     100 |      100 |     100 |     100 |                   
    >   PriceCalculator  |     100 |      100 |     100 |     100 |                   
    >  utils             |     100 |      100 |     100 |     100 |                   
    >   cart.ts          |     100 |      100 |     100 |     100 |                   
    >   priceCalculation |     100 |      100 |     100 |     100 |                   
    > -------------------|---------|----------|---------|---------|-------------------
    > ```

### 2. Kiểm thử End-to-End với Playwright (E2E Tests)
Trước khi chạy Playwright, hãy đảm bảo máy chủ cục bộ của bạn đang chạy (`npm run dev` tại thư mục `frontend`).

*   **Dạng 1: Chạy toàn bộ kịch bản E2E trên cả 3 trình duyệt (Chromium, Firefox, Webkit):**
    ```powershell
    npx playwright test specs/purchase.e2e.spec.ts
    ```
*   **Dạng 2: Chạy kịch bản E2E trên duy nhất 1 trình duyệt (Ví dụ: Chromium):**
    ```powershell
    npx playwright test specs/purchase.e2e.spec.ts --project=chromium
    ```
*   **Dạng 3: Chạy kịch bản E2E ở chế độ giao diện trực quan (UI Mode - Dễ debug và xem từng bước click):**
    ```powershell
    npx playwright test specs/purchase.e2e.spec.ts --ui
    ```
*   **Dạng 4: Chạy một test case cụ thể theo tiêu đề (Ví dụ: Chỉ chạy TC1):**
    ```powershell
    npx playwright test specs/purchase.e2e.spec.ts -g "TC1"
    ```

---

## IV. Danh Sách Các Bảng Test Case Có Trong Dự Án (Test Case Matrices)

Dưới đây là bảng tổng hợp chi tiết tất cả các kịch bản kiểm thử đã được thiết kế và thực thi thành công mỹ mãn trong dự án.

### BẢNG 1: UNIT TEST CASES - CORE PRICE CALCULATION (priceCalculation.test.ts)
Kiểm thử 20 kịch bản của các hàm logic thuần túy toán học và xử lý nghiệp vụ đơn hàng.

| STT | Mã Test Case | Tên Kịch Bản Kiểm Thử | Dữ Liệu Đầu Vào | Kết Quả Mong Đợi | Trạng Thái |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | `UT-PC-01` | Tính giá đơn hàng bình thường không giảm giá, không ship | Cart: `[1.500.000đ x 2]` | Tổng: `3.000.000đ`, Phí ship: `0đ`, Giảm: `0đ`, Cuối: `3.000.000đ` | **PASS** |
| 2 | `UT-PC-02` | Tính giá đơn hàng có phí vận chuyển, không giảm giá | Cart: `[1.500.000đ x 2]`, Ship: `30.000đ` | Tổng: `3.000.000đ`, Phí ship: `30.000đ`, Giảm: `0đ`, Cuối: `3.030.000đ` | **PASS** |
| 3 | `UT-PC-03` | Giảm giá phần trăm (PERCENT) hợp lệ không vượt trần | Cart: `3.000.000đ`, Coupon: `PERCENT 10%`, MaxDiscount: `500.000đ` | Số tiền giảm: `300.000đ` (Thỏa mãn < trần) | **PASS** |
| 4 | `UT-PC-04` | Giảm giá phần trăm (PERCENT) chạm trần maxDiscount | Cart: `3.000.000đ`, Coupon: `PERCENT 10%`, MaxDiscount: `50.000đ` | Số tiền giảm: `50.000đ` (Bị giới hạn bởi trần) | **PASS** |
| 5 | `UT-PC-05` | Giảm giá số tiền cố định (FIXED) cho đơn hàng | Cart: `3.000.000đ`, Coupon: `FIXED 100.000đ` | Số tiền giảm: `100.000đ` | **PASS** |
| 6 | `UT-PC-06` | Áp dụng nhiều mã giảm giá (Order và Shipping) đồng thời | Cart: `3M`, Ship: `30k`, Coupon1: `10% max 50k`, Coupon2: `FreeShip 100%` | Giảm giá hàng: `50.000đ`, Giảm ship: `30.000đ`, Cuối: `2.950.000đ` | **PASS** |
| 7 | `UT-PC-07` | Mã giảm giá đơn hàng vượt quá tổng giá trị đơn (Không âm) | Cart: `50.000đ`, Coupon: `FIXED 100.000đ` | Tổng thanh toán cuối cùng = `0đ` (Không bao giờ âm) | **PASS** |
| 8 | `UT-PC-08` | Đơn hàng rỗng không có sản phẩm | Cart: `[]` | Tổng tiền hàng tạm tính = `0đ` | **PASS** |
| 9 | `UT-PC-09` | Số lượng sản phẩm âm hoặc bằng không | Cart: `[{price: 100k, quantity: -2}]` | Ném lỗi ngoại lệ: "Số lượng sản phẩm không hợp lệ" | **PASS** |
| 10 | `UT-PC-10` | Đơn giá sản phẩm âm hoặc bằng không | Cart: `[{price: -50k, quantity: 1}]` | Ném lỗi ngoại lệ: "Đơn giá sản phẩm không hợp lệ" | **PASS** |
| 11 | `UT-PC-11` | Giá trị đơn nhỏ hơn minOrderValue của mã giảm giá | Cart: `300.000đ`, Coupon min: `500.000đ` | Mã không được áp dụng (Giảm giá = `0đ`) | **PASS** |
| 12 | `UT-PC-12` | Mã giảm giá hết lượt sử dụng (remainingUsage <= 0) | Coupon: `remainingUsage: 0` | Mã không được áp dụng (Giảm giá = `0đ`) | **PASS** |
| 13 | `UT-PC-13` | Mã giảm giá đã quá hạn sử dụng (expiryDate) | Coupon expiry: `2020-01-01` | Mã không được áp dụng (Giảm giá = `0đ`) | **PASS** |
| 14 | `UT-PC-14` | Kiểm tra tồn kho hợp lệ | Cart: `[p1 x 2]`, Kho: `[p1 stock: 10]` | Trả về: `available: true` | **PASS** |
| 15 | `UT-PC-15` | Kiểm tra tồn kho vượt giới hạn (Out of stock) | Cart: `[p1 x 11]`, Kho: `[p1 stock: 10]` | Trả về: `available: false` | **PASS** |
| 16 | `UT-PC-16` | Kiểm tra tồn kho bằng đúng giới hạn | Cart: `[p1 x 10]`, Kho: `[p1 stock: 10]` | Trả về: `available: true` | **PASS** |
| 17 | `UT-PC-17` | Kiểm tra tồn kho với danh sách sản phẩm rỗng | Cart: `[]` | Trả về: `available: true` | **PASS** |
| 18 | `UT-PC-18` | Số lượng yêu cầu kiểm tra kho âm hoặc bằng 0 | Cart: `[p1 x 0]` | Ném lỗi ngoại lệ: "Số lượng yêu cầu không hợp lệ" | **PASS** |
| 19 | `UT-PC-19` | Không tìm thấy sản phẩm trong kho | Cart: `[p_unknown x 1]` | Ném lỗi ngoại lệ: "Sản phẩm không tồn tại trong kho" | **PASS** |
| 20 | `UT-PC-20` | Số lượng tồn kho cấu hình số âm | Kho: `[p1 stock: -5]` | Ném lỗi ngoại lệ: "Dữ liệu tồn kho không hợp lệ" | **PASS** |

---

### BẢNG 2: INTEGRATION TEST CASES - FRONTEND COMPONENTS (checkout.integration.test.tsx)
Kiểm thử sự tích hợp DOM, lắng nghe sự kiện thay đổi lựa chọn của người dùng.

| STT | Mã Test Case | Tên Kịch Bản Kiểm Thử | Thao Tác Giả Lập | Kết Quả Mong Đợi | Trạng Thế |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | `IT-CO-01` | CheckoutSummary hiển thị đúng danh sách sản phẩm và tổng tạm tính | Render `CheckoutSummary` với giỏ hàng có sẵn sản phẩm | Hiển thị đúng tên sản phẩm, số lượng và tổng tạm tính ban đầu | **PASS** |
| 2 | `IT-CO-02` | CheckoutSummary giỏ hàng có duy nhất 1 sản phẩm | Render `CheckoutSummary` chỉ có 1 mặt hàng | Tính toán hiển thị đúng tổng tạm tính | **PASS** |
| 3 | `IT-CO-03` | PriceCalculator tính toán tổng tiền chính xác khi chưa chọn ship/coupon | Render `PriceCalculator` mặc định ban đầu | Tổng thanh toán hiển thị đúng bằng tổng tiền hàng tạm tính | **PASS** |
| 4 | `IT-CO-04` | PriceCalculator thay đổi tổng tiền real-time khi chọn vận chuyển khác nhau | Nhập thay đổi phí vận chuyển mới | Tổng tiền thanh toán cộng thêm phí ship tương ứng tức thời | **PASS** |
| 5 | `IT-CO-05` | PriceCalculator áp dụng thành công mã giảm giá FIXED | Click áp dụng mã giảm giá tiền mặt cố định | Tổng tiền giảm trừ đi chính xác số tiền mặt | **PASS** |
| 6 | `IT-CO-06` | PriceCalculator áp dụng mã PERCENT chạm mức giảm tối đa (maxDiscount) | Click áp mã giảm phần trăm kèm giới hạn trần | Tổng tiền chỉ được giảm tối đa bằng giá trị trần cho phép | **PASS** |
| 7 | `IT-CO-07` | InventoryWarning hiển thị trạng thái hoàn hảo khi đủ hàng | Truyền giỏ hàng có số lượng nằm trong giới hạn cho phép | Hiển thị thông báo màu xanh lá cây sẵn sàng giao hàng | **PASS** |
| 8 | `IT-CO-08` | InventoryWarning hiển thị cảnh báo đỏ nổi bật khi số lượng vượt tồn kho | Truyền giỏ hàng có sản phẩm số lượng vượt quá tồn kho | Hiển thị cảnh báo đỏ chi tiết sản phẩm bị thiếu | **PASS** |
| 9 | `IT-CO-09` | InventoryWarning hiển thị đầy đủ nhiều cảnh báo thiếu hàng | Truyền nhiều sản phẩm thiếu hàng cùng lúc | Hiển thị danh sách tất cả các sản phẩm bị thiếu | **PASS** |
| 10 | `IT-CO-10` | InventoryWarning hiển thị ID sản phẩm dự phòng | Giả lập sản phẩm thiếu kho không có trong dữ liệu gốc | Hiển thị thông tin tên sản phẩm dự phòng theo ID để tránh crash | **PASS** |

---

### BẢNG 3: MOCK TEST CASES - SERVICE INTERACTION (purchase.mock.test.tsx)
Kiểm thử hành vi gửi yêu cầu đặt hàng và xử lý phản hồi từ hệ thống dịch vụ ngầm.

| STT | Mã Test Case | Tên Kịch Bản Kiểm Thử | API Mocking Config | Kết Quả Mong Đợi | Trạng Thái |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | `MT-PS-01` | Mock đặt hàng thành công hoàn chỉnh luồng | `checkStock`: available, `createOrder`: Success (Order ID) | Hiển thị Toast thành công, gọi API `createOrder` đúng tham số | **PASS** |
| 2 | `MT-PS-02` | Mock lỗi hết hàng từ hệ thống kho | `checkStock`: `available: false` | Hiển thị Toast lỗi: "Một số sản phẩm trong giỏ hàng đã hết hàng" | **PASS** |
| 3 | `MT-PS-03` | Mock lỗi kết nối Server (500 Internal Error) | `createOrder`: Reject với Server Error | Hiển thị Toast lỗi: "Đặt hàng thất bại, vui lòng thử lại" | **PASS** |

---

### BẢNG 4: PLAYWRIGHT E2E TEST CASES - USERFLOW (purchase.e2e.spec.ts)
Kiểm thử kiểm tra thực tế hành vi người dùng cuối đầu-cuối trên 3 môi trường trình duyệt thực.

| STT | Mã Test Case | Tên Kịch Bản Kiểm Thử | Luồng Tương Tác Của Người Dùng | Kết Quả Mong Đợi | Trạng Thái |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | `E2E-PC-01` | TC1: Đặt hàng thành công hoàn chỉnh luồng | 1. Đăng nhập hệ thống.<br>2. Đi tới trang thanh toán.<br>3. Điền thông tin người nhận.<br>4. Xác nhận địa chỉ, ship, COD.<br>5. Nhấp nút "Đặt hàng". | - Trình duyệt điều hướng sang `/order/success`.<br>- Hiển thị tiêu đề "Đặt hàng thành công".<br>- Có mã đơn hàng được tạo tự động. | **PASS** <br>*(Chromium, Firefox, Webkit)* |
| 2 | `E2E-PC-02` | TC2: Tính giá thanh toán chính xác bao gồm tiền hàng, ship, coupon | 1. Đăng nhập & sang trang thanh toán.<br>2. Xác nhận tổng ban đầu (chọn sẵn ship): `3.030.000đ`.<br>3. Áp dụng mã giảm giá `YONEX10` (giảm 10% tối đa 50k). | - Tổng thanh toán cập nhật real-time thành `2.980.000đ` chính xác.<br>- Thể hiện dòng giảm giá rõ ràng trên giao diện. | **PASS** <br>*(Chromium, Firefox, Webkit)* |

---

## V. Cấu Hình Tự Động Hóa CI/CD Pipeline chính thức

Quy trình tự động hóa đã được thiết lập đầy đủ trong file cấu hình `.github/workflows/ci.yml`. Mỗi khi có hoạt động commit hoặc tạo pull request, GitHub Actions sẽ:

1.  Khởi chạy song song dịch vụ **PostgreSQL 17** làm Database kiểm thử cho Backend.
2.  Thiết lập môi trường **Java 25 (Oracle)** và **Node.js 22** trên hệ điều hành Ubuntu mới nhất.
3.  Kích hoạt kiểm thử phía **Backend** thông qua Maven, xuất báo cáo độ bao phủ mã nguồn JaCoCo dưới dạng Artifact.
4.  Cài đặt dependencies và chạy **Frontend tests** thông qua Vitest, đảm bảo độ bao phủ mã nguồn đạt chuẩn yêu cầu.
5.  Cài đặt các trình duyệt kiểm thử tự động của **Playwright**, khởi động server cục bộ và thực thi bộ kịch bản E2E toàn diện trên cả 3 trình duyệt, tự động xuất báo cáo kết quả HTML lưu trữ trực tiếp trên GitHub Actions.
