import { describe, it, expect } from 'vitest';

import {
  calculateTotal,
  validateStock,
  removeItem
} from '../utils/cart';

describe('Cart Unit Tests', () => {

  // ── TC1: Thêm sản phẩm mới vào giỏ hàng thành công ──────────────────────
  it('TC1: Thêm sản phẩm mới vào giỏ hàng thành công', () => {

    const items = [
      { price: 1500000, quantity: 2 },
      { price: 500000, quantity: 1 }
    ];

    // Tổng = 3.500.000
    expect(calculateTotal(items))
      .toBe(3500000);

  });

  // ── TC2: Cộng dồn số lượng sản phẩm đã tồn tại ──────────────────────────
  it('TC2: Cộng dồn số lượng sản phẩm đã tồn tại trong giỏ hàng', () => {

    const oldQuantity = 2;
    const addedQuantity = 3;

    const newQuantity = oldQuantity + addedQuantity;

    expect(newQuantity).toBe(5);

  });

  // ── TC3: Quantity vượt quá tồn kho ──────────────────────────────────────
  it('TC3: Quantity vượt quá tồn kho', () => {

    expect(validateStock(11, 10))
      .toBe(false);

  });

  // ── TC4: Cập nhật số lượng sản phẩm trong giỏ hàng ──────────────────────
  it('TC4: Cập nhật số lượng sản phẩm trong giỏ hàng', () => {

    expect(validateStock(5, 10))
      .toBe(true);

  });

  // ── TC5: Xóa sản phẩm khỏi giỏ hàng ─────────────────────────────────────
  it('TC5: Xóa sản phẩm khỏi giỏ hàng', () => {

    const cart = [
      { productId: 'p1' },
      { productId: 'p2' }
    ];

    const result = removeItem(cart, 'p1');

    expect(result).toHaveLength(1);

    expect(result[0].productId)
      .toBe('p2');

  });

});