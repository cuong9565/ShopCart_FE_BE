import React, { useState } from 'react';
import { calculateOrderPrice, type CouponInput } from '../utils/priceCalculation';

interface PriceCalculatorProps {
  initialSubtotal: number;
}

export const PriceCalculator: React.FC<PriceCalculatorProps> = ({ initialSubtotal }) => {
  const [shippingFee, setShippingFee] = useState<number>(30000);
  const [couponType, setCouponType] = useState<'NONE' | 'PERCENT' | 'FIXED'>('NONE');
  const [couponValue, setCouponValue] = useState<number>(10);
  const [maxDiscount, setMaxDiscount] = useState<number>(50000);
  const [minOrder, setMinOrder] = useState<number>(100000);

  // Map settings to calculateOrderPrice input
  const coupon: CouponInput | null = couponType === 'NONE' ? null : {
    discountType: couponType,
    discountValue: couponValue,
    maxDiscount: couponType === 'PERCENT' ? maxDiscount : null,
    minOrderValue: minOrder,
  };

  const pricing = calculateOrderPrice(
    [{ price: initialSubtotal, quantity: 1 }],
    coupon,
    shippingFee
  );

  return (
    <div className="bg-white rounded-2xl shadow-sm p-6 border border-gray-100">
      <h3 className="text-lg font-bold text-gray-900 mb-4">Công cụ tính giá tự động</h3>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-6">
        {/* Shipping Fee Input */}
        <div>
          <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
            Phí vận chuyển (VND)
          </label>
          <input
            type="number"
            data-testid="input-shipping"
            value={shippingFee}
            onChange={(e) => setShippingFee(Number(e.target.value))}
            className="w-full border border-gray-200 rounded-xl px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/40"
          />
        </div>

        {/* Coupon Type Select */}
        <div>
          <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
            Loại mã giảm giá
          </label>
          <select
            data-testid="select-coupon-type"
            value={couponType}
            onChange={(e) => setCouponType(e.target.value as any)}
            className="w-full border border-gray-200 rounded-xl px-4 py-2 text-sm bg-white focus:outline-none focus:ring-2 focus:ring-primary/40"
          >
            <option value="NONE">Không áp dụng</option>
            <option value="PERCENT">Giảm phần trăm (%)</option>
            <option value="FIXED">Giảm tiền cố định (đ)</option>
          </select>
        </div>

        {/* Coupon Value Input */}
        {couponType !== 'NONE' && (
          <div>
            <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
              Giá trị giảm ({couponType === 'PERCENT' ? '%' : 'đ'})
            </label>
            <input
              type="number"
              data-testid="input-coupon-value"
              value={couponValue}
              onChange={(e) => setCouponValue(Number(e.target.value))}
              className="w-full border border-gray-200 rounded-xl px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/40"
            />
          </div>
        )}

        {/* Max Discount Cap Input for PERCENT coupons */}
        {couponType === 'PERCENT' && (
          <div>
            <label className="block text-xs font-semibold text-gray-500 uppercase tracking-wider mb-1">
              Giới hạn giảm tối đa (VND)
            </label>
            <input
              type="number"
              data-testid="input-max-discount"
              value={maxDiscount}
              onChange={(e) => setMaxDiscount(Number(e.target.value))}
              className="w-full border border-gray-200 rounded-xl px-4 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-primary/40"
            />
          </div>
        )}
      </div>

      {/* Bill receipt display */}
      <div className="bg-gray-50 rounded-xl p-4 space-y-2 border border-gray-100 text-sm">
        <div className="flex justify-between text-gray-600">
          <span>Tiền hàng:</span>
          <span data-testid="calc-subtotal">{pricing.subtotal.toLocaleString('vi-VN')}đ</span>
        </div>
        <div className="flex justify-between text-gray-600">
          <span>Phí vận chuyển:</span>
          <span data-testid="calc-shipping">{pricing.shipping.toLocaleString('vi-VN')}đ</span>
        </div>
        {pricing.discount > 0 && (
          <div className="flex justify-between text-green-600 font-semibold">
            <span>Giảm giá:</span>
            <span data-testid="calc-discount">-{pricing.discount.toLocaleString('vi-VN')}đ</span>
          </div>
        )}
        <div className="flex justify-between font-bold text-gray-900 border-t border-gray-200 pt-3 mt-1 text-base">
          <span>Tổng thanh toán:</span>
          <span className="text-primary text-lg" data-testid="calc-total">
            {pricing.total.toLocaleString('vi-VN')}đ
          </span>
        </div>
      </div>
    </div>
  );
};

export default PriceCalculator;
