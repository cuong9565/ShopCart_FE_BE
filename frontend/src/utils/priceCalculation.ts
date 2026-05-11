/**
 * priceCalculation.ts
 * Pure utility functions for order price calculation and inventory checking.
 * These functions are framework-agnostic and fully unit-testable.
 */

// ─── Types ────────────────────────────────────────────────────────────────────

export interface OrderItem {
  price: number;    // Unit price in VND
  quantity: number; // Quantity ordered
}

export type CouponType = 'PERCENT' | 'FIXED';

export interface CouponInput {
  discountType: CouponType;
  discountValue: number;        // % value (0-100) or fixed VND amount
  minOrderValue?: number;       // Minimum subtotal required to apply
  maxDiscount?: number | null;  // Cap for PERCENT coupons
}

export interface OrderPriceResult {
  subtotal: number;   // Sum of (price × quantity) for all items
  discount: number;   // Coupon discount applied (0 if no coupon or not eligible)
  shipping: number;   // Shipping fee passed in
  total: number;      // subtotal + shipping - discount (≥ 0)
}

export interface InventoryCheckItem {
  productId: string;
  requested: number; // Quantity the user wants to order
  stock: number;     // Actual available stock
}

export interface InventoryCheckResult {
  available: boolean;
  insufficientItems: Array<{
    productId: string;
    requested: number;
    stock: number;
  }>;
}

// ─── calculateOrderPrice ──────────────────────────────────────────────────────

/**
 * Calculates full order pricing including coupon discounts and shipping.
 *
 * @param items       - Array of order items with price and quantity
 * @param coupon      - Optional coupon to apply (null = no coupon)
 * @param shippingFee - Flat shipping fee in VND
 * @returns OrderPriceResult with subtotal, discount, shipping, total
 */
export function calculateOrderPrice(
  items: OrderItem[],
  coupon: CouponInput | null,
  shippingFee: number
): OrderPriceResult {
  // 1. Calculate subtotal
  const subtotal = items.reduce(
    (sum, item) => sum + item.price * item.quantity,
    0
  );

  // 2. Calculate discount
  let discount = 0;

  if (coupon !== null) {
    const meetsMinOrder =
      coupon.minOrderValue === undefined || subtotal >= coupon.minOrderValue;

    if (meetsMinOrder) {
      if (coupon.discountType === 'FIXED') {
        discount = coupon.discountValue;
      } else {
        // PERCENT
        discount = Math.round((subtotal * coupon.discountValue) / 100);

        // Apply maxDiscount cap if specified
        if (coupon.maxDiscount != null && discount > coupon.maxDiscount) {
          discount = coupon.maxDiscount;
        }
      }

      // Discount cannot exceed the subtotal
      if (discount > subtotal) {
        discount = subtotal;
      }
    }
  }

  // 3. Calculate total (never negative)
  const total = Math.max(0, subtotal + shippingFee - discount);

  return { subtotal, discount, shipping: shippingFee, total };
}

// ─── checkInventoryAvailability ───────────────────────────────────────────────

/**
 * Checks whether all requested items can be fulfilled from current stock.
 *
 * @param items - Array of items with productId, requested quantity, and stock
 * @returns InventoryCheckResult with overall availability and list of problem items
 */
export function checkInventoryAvailability(
  items: InventoryCheckItem[]
): InventoryCheckResult {
  const insufficientItems = items
    .filter((item) => item.requested > item.stock)
    .map(({ productId, requested, stock }) => ({ productId, requested, stock }));

  return {
    available: insufficientItems.length === 0,
    insufficientItems,
  };
}
