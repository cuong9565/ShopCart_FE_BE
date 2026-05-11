/**
 * priceCalculation.test.ts
 * Unit tests for calculateOrderPrice() and checkInventoryAvailability()
 * using Vitest — following TDD approach.
 */
import { describe, test, expect } from 'vitest';
import {
  calculateOrderPrice,
  checkInventoryAvailability,
  type OrderItem,
  type CouponInput,
  type InventoryCheckItem,
} from '../utils/priceCalculation';

// ═══════════════════════════════════════════════════════════════════════════
// a) calculateOrderPrice() Tests
// ═══════════════════════════════════════════════════════════════════════════

describe('calculateOrderPrice()', () => {

  // ── TC1: Tính tổng giá không có giảm giá ──────────────────────────────
  test('TC1: Tính tổng giá không có giảm giá', () => {
    const items: OrderItem[] = [
      { price: 15_000_000, quantity: 2 },
      { price: 500_000,    quantity: 1 },
    ];
    const result = calculateOrderPrice(items, null, 50_000);

    expect(result.subtotal).toBe(30_500_000);
    expect(result.discount).toBe(0);
    expect(result.shipping).toBe(50_000);
    expect(result.total).toBe(30_550_000);
  });

  // ── TC2: Áp dụng coupon giảm % 10% ────────────────────────────────────
  test('TC2: Áp dụng coupon giảm % 10%', () => {
    const items: OrderItem[] = [
      { price: 1_000_000, quantity: 2 },
    ];
    const coupon: CouponInput = { discountType: 'PERCENT', discountValue: 10 };

    const result = calculateOrderPrice(items, coupon, 30_000);

    expect(result.subtotal).toBe(2_000_000);
    expect(result.discount).toBe(200_000);   // 10% of 2_000_000
    expect(result.shipping).toBe(30_000);
    expect(result.total).toBe(1_830_000);    // 2_000_000 + 30_000 - 200_000
  });

  // ── TC3: Áp dụng coupon giảm % 20% ────────────────────────────────────
  test('TC3: Áp dụng coupon giảm % 20%', () => {
    const items: OrderItem[] = [
      { price: 500_000, quantity: 4 },
    ];
    const coupon: CouponInput = { discountType: 'PERCENT', discountValue: 20 };

    const result = calculateOrderPrice(items, coupon, 0);

    expect(result.subtotal).toBe(2_000_000);
    expect(result.discount).toBe(400_000);   // 20% of 2_000_000
    expect(result.total).toBe(1_600_000);
  });

  // ── TC4: Coupon % bị giới hạn bởi maxDiscount ─────────────────────────
  test('TC4: Coupon % bị giới hạn bởi maxDiscount', () => {
    const items: OrderItem[] = [
      { price: 5_000_000, quantity: 2 },
    ];
    const coupon: CouponInput = {
      discountType: 'PERCENT',
      discountValue: 20,     // 20% of 10_000_000 = 2_000_000
      maxDiscount: 500_000,  // capped at 500_000
    };

    const result = calculateOrderPrice(items, coupon, 50_000);

    expect(result.subtotal).toBe(10_000_000);
    expect(result.discount).toBe(500_000);   // capped
    expect(result.total).toBe(9_550_000);
  });

  // ── TC5: Áp dụng coupon giảm số tiền cố định (FIXED) ─────────────────
  test('TC5: Áp dụng coupon giảm số tiền cố định', () => {
    const items: OrderItem[] = [
      { price: 300_000, quantity: 3 },
    ];
    const coupon: CouponInput = {
      discountType: 'FIXED',
      discountValue: 100_000,
    };

    const result = calculateOrderPrice(items, coupon, 25_000);

    expect(result.subtotal).toBe(900_000);
    expect(result.discount).toBe(100_000);
    expect(result.shipping).toBe(25_000);
    expect(result.total).toBe(825_000);     // 900_000 + 25_000 - 100_000
  });

  // ── TC6: Tính phí vận chuyển riêng lẻ không có coupon ─────────────────
  test('TC6: Tính phí vận chuyển', () => {
    const items: OrderItem[] = [{ price: 200_000, quantity: 1 }];
    const shippingFee = 35_000;

    const result = calculateOrderPrice(items, null, shippingFee);

    expect(result.shipping).toBe(35_000);
    expect(result.total).toBe(235_000);
  });

  // ── TC7: Tổng cuối = subtotal + shipping - discount ────────────────────
  test('TC7: Tổng cuối cùng (subtotal + shipping - discount)', () => {
    const items: OrderItem[] = [
      { price: 2_000_000, quantity: 1 },
      { price: 500_000,   quantity: 2 },
    ];
    const coupon: CouponInput = { discountType: 'FIXED', discountValue: 200_000 };

    const result = calculateOrderPrice(items, coupon, 40_000);

    expect(result.subtotal).toBe(3_000_000);
    expect(result.discount).toBe(200_000);
    expect(result.shipping).toBe(40_000);
    expect(result.total).toBe(2_840_000);   // 3_000_000 + 40_000 - 200_000
  });

  // ── TC8: Coupon không áp dụng vì chưa đạt minOrderValue ───────────────
  test('TC8: Coupon không áp dụng nếu chưa đạt giá trị đơn tối thiểu', () => {
    const items: OrderItem[] = [{ price: 100_000, quantity: 1 }];
    const coupon: CouponInput = {
      discountType: 'PERCENT',
      discountValue: 50,
      minOrderValue: 500_000,  // subtotal 100_000 < 500_000
    };

    const result = calculateOrderPrice(items, coupon, 20_000);

    expect(result.subtotal).toBe(100_000);
    expect(result.discount).toBe(0);        // coupon not applied
    expect(result.total).toBe(120_000);
  });

  // ── TC9: Coupon áp dụng khi đúng bằng minOrderValue (boundary) ────────
  test('TC9: Coupon áp dụng khi đúng bằng minOrderValue (boundary)', () => {
    const items: OrderItem[] = [{ price: 500_000, quantity: 1 }];
    const coupon: CouponInput = {
      discountType: 'FIXED',
      discountValue: 50_000,
      minOrderValue: 500_000,  // exactly meets minimum
    };

    const result = calculateOrderPrice(items, coupon, 0);

    expect(result.discount).toBe(50_000);
    expect(result.total).toBe(450_000);
  });

  // ── TC10: Discount không vượt quá subtotal ────────────────────────────
  test('TC10: Discount không vượt quá subtotal', () => {
    const items: OrderItem[] = [{ price: 50_000, quantity: 1 }];
    const coupon: CouponInput = {
      discountType: 'FIXED',
      discountValue: 200_000,  // coupon > subtotal
    };

    const result = calculateOrderPrice(items, coupon, 0);

    expect(result.subtotal).toBe(50_000);
    expect(result.discount).toBe(50_000);  // capped to subtotal
    expect(result.total).toBe(0);          // never negative
  });

  // ── TC11: Giỏ hàng trống ─────────────────────────────────────────────
  test('TC11: Giỏ hàng trống trả về subtotal = 0', () => {
    const result = calculateOrderPrice([], null, 30_000);

    expect(result.subtotal).toBe(0);
    expect(result.discount).toBe(0);
    expect(result.shipping).toBe(30_000);
    expect(result.total).toBe(30_000);
  });

  // ── TC12: Phí vận chuyển = 0 (miễn phí ship) ─────────────────────────
  test('TC12: Phí vận chuyển = 0 (miễn phí ship)', () => {
    const items: OrderItem[] = [{ price: 1_000_000, quantity: 1 }];

    const result = calculateOrderPrice(items, null, 0);

    expect(result.shipping).toBe(0);
    expect(result.total).toBe(1_000_000);
  });
});

// ═══════════════════════════════════════════════════════════════════════════
// b) checkInventoryAvailability() Tests
// ═══════════════════════════════════════════════════════════════════════════

describe('checkInventoryAvailability()', () => {

  // ── TC1: Tất cả sản phẩm đủ hàng ─────────────────────────────────────
  test('TC1: Tất cả sản phẩm đủ hàng → available = true', () => {
    const items: InventoryCheckItem[] = [
      { productId: 'p1', requested: 2, stock: 10 },
      { productId: 'p2', requested: 5, stock: 5 },
    ];

    const result = checkInventoryAvailability(items);

    expect(result.available).toBe(true);
    expect(result.insufficientItems).toHaveLength(0);
  });

  // ── TC2: Một sản phẩm thiếu hàng ─────────────────────────────────────
  test('TC2: Một sản phẩm thiếu hàng → available = false', () => {
    const items: InventoryCheckItem[] = [
      { productId: 'p1', requested: 5, stock: 3 },
      { productId: 'p2', requested: 1, stock: 10 },
    ];

    const result = checkInventoryAvailability(items);

    expect(result.available).toBe(false);
    expect(result.insufficientItems).toHaveLength(1);
    expect(result.insufficientItems[0].productId).toBe('p1');
    expect(result.insufficientItems[0].requested).toBe(5);
    expect(result.insufficientItems[0].stock).toBe(3);
  });

  // ── TC3: Nhiều sản phẩm thiếu hàng ───────────────────────────────────
  test('TC3: Nhiều sản phẩm thiếu hàng → trả về tất cả', () => {
    const items: InventoryCheckItem[] = [
      { productId: 'p1', requested: 10, stock: 2 },
      { productId: 'p2', requested: 8,  stock: 3 },
      { productId: 'p3', requested: 1,  stock: 5 },
    ];

    const result = checkInventoryAvailability(items);

    expect(result.available).toBe(false);
    expect(result.insufficientItems).toHaveLength(2);
    expect(result.insufficientItems.map(i => i.productId)).toEqual(['p1', 'p2']);
  });

  // ── TC4: Đúng bằng tồn kho (boundary) ────────────────────────────────
  test('TC4: Số lượng đặt đúng bằng tồn kho → available = true', () => {
    const items: InventoryCheckItem[] = [
      { productId: 'p1', requested: 5, stock: 5 },
    ];

    const result = checkInventoryAvailability(items);

    expect(result.available).toBe(true);
    expect(result.insufficientItems).toHaveLength(0);
  });

  // ── TC5: Số lượng đặt = 0 luôn hợp lệ ───────────────────────────────
  test('TC5: Số lượng đặt = 0 → hợp lệ', () => {
    const items: InventoryCheckItem[] = [
      { productId: 'p1', requested: 0, stock: 5 },
    ];

    const result = checkInventoryAvailability(items);

    expect(result.available).toBe(true);
  });

  // ── TC6: Sản phẩm hết hàng (stock = 0) ───────────────────────────────
  test('TC6: Sản phẩm hết hàng (stock = 0) → available = false', () => {
    const items: InventoryCheckItem[] = [
      { productId: 'p1', requested: 1, stock: 0 },
    ];

    const result = checkInventoryAvailability(items);

    expect(result.available).toBe(false);
    expect(result.insufficientItems[0].productId).toBe('p1');
  });

  // ── TC7: Giỏ hàng trống → luôn hợp lệ ───────────────────────────────
  test('TC7: Danh sách sản phẩm trống → available = true', () => {
    const result = checkInventoryAvailability([]);

    expect(result.available).toBe(true);
    expect(result.insufficientItems).toHaveLength(0);
  });

  // ── TC8: Tất cả sản phẩm đều thiếu hàng ──────────────────────────────
  test('TC8: Tất cả sản phẩm đều thiếu hàng', () => {
    const items: InventoryCheckItem[] = [
      { productId: 'p1', requested: 10, stock: 1 },
      { productId: 'p2', requested: 20, stock: 5 },
    ];

    const result = checkInventoryAvailability(items);

    expect(result.available).toBe(false);
    expect(result.insufficientItems).toHaveLength(2);
  });
});
