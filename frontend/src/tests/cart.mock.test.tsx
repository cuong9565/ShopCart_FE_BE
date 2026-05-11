/**
 * cart.mock.test.tsx
 * Unit and integration mocking tests for cart and inventory services
 * using Vitest's mocking capabilities (vi.mock).
 */

import { describe, test, expect, vi, beforeEach } from 'vitest';
import { cartService } from '../services/cartService';
import * as inventoryService from '../services/inventoryService';

// Mock services
vi.mock('../services/cartService', () => ({
  cartService: {
    addToCart: vi.fn(),
  },
}));

vi.mock('../services/inventoryService', () => ({
  checkStock: vi.fn(),
}));

// Hàm giả lập luồng thêm sản phẩm vào giỏ hàng
async function performAddToCartFlow(
  productId: string,
  quantity: number
) {
  // 1. Kiểm tra tồn kho
  const stockCheck = await inventoryService.checkStock([
    { productId, quantity },
  ]);

  if (!stockCheck.available) {
    throw new Error('Sản phẩm vượt quá số lượng tồn kho!');
  }

  // 2. Thêm vào giỏ hàng
  await cartService.addToCart(productId, quantity);

  // 3. Trả kết quả giả lập
  return {
    success: true,
    productId,
    quantity,
  };
}

describe('Kiểm thử giả lập (Cart Mock Tests)', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  // ── TC1: Thêm sản phẩm thành công ────────────────────────────────────────
  test('TC1: Giả lập thêm sản phẩm vào giỏ hàng thành công', async () => {
    // Mock còn hàng
    vi.mocked(inventoryService.checkStock).mockResolvedValue({
      available: true,
    });

    // Mock addToCart thành công
    vi.mocked(cartService.addToCart).mockResolvedValue(undefined);

    const result = await performAddToCartFlow('P001', 2);

    // Kiểm tra kết quả
    expect(result.success).toBe(true);
    expect(result.productId).toBe('P001');
    expect(result.quantity).toBe(2);

    // Verify checkStock
    expect(inventoryService.checkStock).toHaveBeenCalledTimes(1);

    expect(inventoryService.checkStock).toHaveBeenCalledWith([
      {
        productId: 'P001',
        quantity: 2,
      },
    ]);

    // Verify addToCart
    expect(cartService.addToCart).toHaveBeenCalledTimes(1);

    expect(cartService.addToCart).toHaveBeenCalledWith(
      'P001',
      2
    );
  });

  // ── TC2: Hết hàng ────────────────────────────────────────────────────────
  test('TC2: Giả lập thêm sản phẩm thất bại do vượt tồn kho', async () => {
    // Mock hết hàng
    vi.mocked(inventoryService.checkStock).mockResolvedValue({
      available: false,
    });

    // Expect throw error
    await expect(
      performAddToCartFlow('P002', 10)
    ).rejects.toThrow('Sản phẩm vượt quá số lượng tồn kho!');

    // Verify/
    expect(inventoryService.checkStock).toHaveBeenCalledTimes(1);

    // addToCart không được gọi
    expect(cartService.addToCart).not.toHaveBeenCalled();
  });

  // ── TC3: Lỗi server ──────────────────────────────────────────────────────
  test('TC3: Giả lập lỗi hệ thống khi thêm vào giỏ hàng', async () => {
    // Mock còn hàng
    vi.mocked(inventoryService.checkStock).mockResolvedValue({
      available: true,
    });

    // Mock lỗi server
    vi.mocked(cartService.addToCart).mockRejectedValue(
      new Error('500 Internal Server Error')
    );

    // Expect throw error
    await expect(
      performAddToCartFlow('P003', 1)
    ).rejects.toThrow('500 Internal Server Error');

    // Verify
    expect(inventoryService.checkStock).toHaveBeenCalledTimes(1);

    expect(cartService.addToCart).toHaveBeenCalledTimes(1);
  });
});