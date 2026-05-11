/**
 * purchase.mock.test.tsx
 * Unit and integration mocking tests for order and inventory services
 * using Vitest's mocking capabilities (vi.mock).
 */
import { describe, test, expect, vi, beforeEach } from 'vitest';
import * as orderService from '../services/orderService';
import * as inventoryService from '../services/inventoryService';

// Thiết lập Mock cho các Services để không gọi API thật
vi.mock('../services/orderService');
vi.mock('../services/inventoryService');

// Định nghĩa hàm quy trình mua sắm giả lập (Purchase Flow) để thực hiện kiểm thử tích hợp
async function performPurchaseFlow(items: Array<{ productId: string; quantity: number }>, orderData: any) {
  // 1. Kiểm tra tồn kho trước
  const stockCheck = await inventoryService.checkStock(items);
  if (!stockCheck.available) {
    throw new Error('Sản phẩm trong giỏ hàng đã hết hàng!');
  }

  // 2. Nếu đủ hàng, tiến hành tạo đơn hàng
  return await orderService.createOrder(orderData);
}

describe('Kiểm thử giả lập (Purchase Mock Tests)', () => {
  beforeEach(() => {
    // Khôi phục trạng thái ban đầu của tất cả các mock trước mỗi test case
    vi.clearAllMocks();
  });

  // ── TC1: Đặt hàng thành công ──────────────────────────────────────────────
  test('TC1: Giả lập đặt hàng thành công (Success Scenario)', async () => {
    // Cài đặt dữ liệu trả về cho Mock của checkStock
    vi.mocked(inventoryService.checkStock).mockResolvedValue({
      available: true,
    });

    // Cài đặt dữ liệu trả về cho Mock của createOrder
    vi.mocked(orderService.createOrder).mockResolvedValue({
      orderId: 'ORD-001',
      status: 'PENDING',
      totalPrice: 30550000,
    });

    // Chạy quy trình đặt hàng
    const items = [{ productId: 'P001', quantity: 2 }];
    const orderData = { items, addressId: 'A01', paymentMethod: 'COD' };
    
    const result = await performPurchaseFlow(items, orderData);

    // Kiểm tra kết quả trả về khớp với Mock
    expect(result.orderId).toBe('ORD-001');
    expect(result.status).toBe('PENDING');
    expect(result.totalPrice).toBe(30550000);

    // Xác minh (Verify) cuộc gọi Mock của orderService.createOrder
    expect(orderService.createOrder).toHaveBeenCalledTimes(1);
    expect(orderService.createOrder).toHaveBeenCalledWith(orderData);

    // Xác minh cuộc gọi Mock của inventoryService.checkStock
    expect(inventoryService.checkStock).toHaveBeenCalledTimes(1);
    expect(inventoryService.checkStock).toHaveBeenCalledWith(
      expect.arrayContaining([
        expect.objectContaining({ productId: 'P001' })
      ])
    );
  });

  // ── TC2: Thất bại do thiếu hàng trong kho ──────────────────────────────────
  test('TC2: Giả lập đặt hàng thất bại do hết hàng trong kho (Failure Scenario)', async () => {
    // Cài đặt Mock checkStock trả về hết hàng (available = false)
    vi.mocked(inventoryService.checkStock).mockResolvedValue({
      available: false,
    });

    const items = [{ productId: 'P002', quantity: 10 }];
    const orderData = { items, addressId: 'A01', paymentMethod: 'COD' };

    // Kiểm tra việc ném ra ngoại lệ khi mua hàng
    await expect(performPurchaseFlow(items, orderData)).rejects.toThrow(
      'Sản phẩm trong giỏ hàng đã hết hàng!'
    );

    // Xác minh: checkStock đã được gọi nhưng createOrder TUYỆT ĐỐI KHÔNG được gọi
    expect(inventoryService.checkStock).toHaveBeenCalledTimes(1);
    expect(orderService.createOrder).not.toHaveBeenCalled();
  });

  // ── TC3: Thất bại do lỗi máy chủ (Server Error) ──────────────────────────
  test('TC3: Giả lập lỗi hệ thống từ Server khi tạo đơn (Failure Scenario)', async () => {
    // Kho hàng báo còn hàng
    vi.mocked(inventoryService.checkStock).mockResolvedValue({
      available: true,
    });

    // Server ném ra lỗi 500 Internal Server Error
    vi.mocked(orderService.createOrder).mockRejectedValue(
      new Error('500 Internal Server Error')
    );

    const items = [{ productId: 'P003', quantity: 1 }];
    const orderData = { items, addressId: 'A02', paymentMethod: 'VNPAY' };

    // Kiểm tra luồng xử lý ngoại lệ từ Server
    await expect(performPurchaseFlow(items, orderData)).rejects.toThrow(
      '500 Internal Server Error'
    );

    // Xác minh cả hai mock đều đã được gọi đúng thứ tự
    expect(inventoryService.checkStock).toHaveBeenCalledTimes(1);
    expect(orderService.createOrder).toHaveBeenCalledTimes(1);
  });
});
