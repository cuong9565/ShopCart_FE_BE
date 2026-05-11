import { describe, it, expect } from 'vitest';

import {
  calculateTotal,
  validateStock,
  removeItem
} from '../utils/cart';

describe('Cart Unit Tests', () => {

  it('TC1: Tính tổng tiền đúng', () => {
    const items = [
      { price: 1500000, quantity: 2 },
      { price: 500000, quantity: 1 }
    ];

    expect(calculateTotal(items))
      .toBe(3500000);
  });

  it('TC2: Vượt tồn kho', () => {
    expect(validateStock(11, 10))
      .toBe(false);
  });

  it('TC3: Tồn kho hợp lệ', () => {
    expect(validateStock(5, 10))
      .toBe(true);
  });

  it('TC4: Xóa sản phẩm khỏi cart', () => {
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