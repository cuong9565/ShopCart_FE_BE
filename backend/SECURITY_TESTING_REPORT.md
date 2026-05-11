# BÁO CÁO KIỂM THỬ BẢO MẬT (SECURITY TESTING REPORT)

**Dự án:** ShopCart - Module Cart (Giỏ hàng)
**Kỹ thuật:** Integration Testing với Spring Security & MockMvc
**Môi trường thực thi:** Java 21, Spring Boot 4.0.6, JUnit 5

---

## a/ Thiết kế Test Cases (0.25 điểm)

Hệ thống tập trung kiểm thử các rủi ro nguy hiểm nhất đối với luồng giỏ hàng, đặc biệt là lỗi truy cập dữ liệu chéo (IDOR) và kiểm soát quyền truy cập API.

| ID        | Nhóm rủi ro | Mô tả kịch bản test | Dữ liệu/Request mô phỏng | Kết quả mong đợi |
| :---      | :---        | :---                | :---                     | :---             |
| **ST_01** | **IDOR**    | User A cố tình thay đổi số lượng sản phẩm trong giỏ hàng của User B thông qua ID. | `PUT /api/cart` <br> Body: `{"productId": "[RANDOM_UUID]", "quantity": 100}` | Hệ thống chặn đứng yêu cầu và ném ra Exception/Error 4xx phù hợp. |
| **ST_02** | **Broken Access Control** | Truy cập API quản lý giỏ hàng mà không cung cấp mã xác thực (Token/Session). | `GET /api/cart` <br> Header: `Authorization: None` | Trả về mã lỗi **401 Unauthorized**. |

---

## b/ Thực thi và Ghi nhận kết quả (0.25 điểm)

Việc thực thi được thực hiện thông qua lớp kiểm thử `CartSecurityTest.java` sử dụng **Custom Security Context Factory** để giả lập đối tượng `CustomUserDetails` mà không cần truy vấn cơ sở dữ liệu thật.

### 1. Nhật ký thực thi (Execution Log)
* **Lệnh chạy:** `mvn test -Dtest=CartSecurityTest`
* **Kết quả ghi nhận từ Console:**
    * Hệ thống thực hiện truy vấn SQL có điều kiện ràng buộc giữa `user_id` và `product_id`.
    * **Log Hibernate:** `select ... from cart_item where user_id=? and product_id=?`
    * **Trạng thái:** `Tests run: 1, Failures: 0, Errors: 0, Skipped: 0`
    * **Kết quả cuối cùng:** <span style="color:green">**BUILD SUCCESS**</span>

### 2. Minh chứng kết quả
* **Trạng thái:** **PASSED** (Hệ thống an toàn).
* **Chi tiết:** Khi gửi request với ID sản phẩm của người khác, logic tại tầng Service không tìm thấy bản ghi tương ứng gắn với `userId` hiện tại. Hệ thống đã chặn đứng hành vi này bằng ngoại lệ: `java.lang.IllegalArgumentException: Cart item not found for user and product`.

---

## c/ Tác động bảo mật và Đề xuất khắc phục (0.25 điểm)

### 1. Tác động bảo mật
* **Rò rỉ thông tin cá nhân (Privacy Breach):** Kẻ tấn công có thể dò quét ID sản phẩm để xem hoặc thao túng hành vi mua sắm của bất kỳ khách hàng nào.
* **Sai lệch dữ liệu đơn hàng:** Việc thay đổi số lượng hoặc sản phẩm trái phép trong giỏ hàng gây hậu quả nghiêm trọng trong khâu thanh toán và xử lý đơn hàng thực tế.
* **Tổn hại uy tín:** Các lỗi bảo mật cơ bản như IDOR khiến người dùng mất niềm tin vào tính an toàn của nền tảng thương mại điện tử.

### 2. Biện pháp khắc phục
* **Xác thực định danh tại Server:** Tuyệt đối không sử dụng `userId` gửi lên từ Client (RequestBody/Param). Luôn lấy `userId` trực tiếp từ `SecurityContext` (Principal) sau khi đã xác thực Token thành công.
* **Ràng buộc truy vấn kép:** Luôn áp dụng điều kiện lọc kép `WHERE product_id = :pId AND user_id = :uId` cho mọi tác vụ Cập nhật (Update) hoặc Xóa (Delete) trong giỏ hàng.
* **Sử dụng UUID thay cho ID tăng dần:** Sử dụng chuỗi ngẫu nhiên UUID giúp ngăn chặn việc kẻ tấn công thực hiện kỹ thuật "Id Enumeration" (dò quét ID theo thứ tự).
* **Quản lý lỗi tập trung:** Triển khai `@ControllerAdvice` để chuyển đổi các Exception logic thành mã lỗi HTTP chuẩn (400, 403), tránh lộ thông tin Stack Trace chi tiết ra môi trường bên ngoài.