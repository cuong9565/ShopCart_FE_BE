import { useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faCheckCircle,
  faBox,
  faTruck,
  faCreditCard,
  faTag,
} from '@fortawesome/free-solid-svg-icons';
import type { OrderResponse } from '../types';

const OrderSuccessPage = () => {
  const location = useLocation();
  const order = location.state?.order as OrderResponse | undefined;

  // Reset cart badge in Navbar — cart was cleared by backend after placing order
  useEffect(() => {
    window.dispatchEvent(new Event('cartUpdated'));
  }, []);

  if (!order) {
    return (
      <div className="max-w-xl mx-auto px-4 py-20 text-center">
        <h2 className="text-2xl font-bold text-gray-700 mb-4">Không tìm thấy thông tin đơn hàng</h2>
        <Link to="/" className="bg-primary text-white px-6 py-3 rounded-xl font-semibold hover:bg-primary-dark transition">
          Về trang chủ
        </Link>
      </div>
    );
  }

  const { shippingInfo, paymentInfo, items, pricingInfo, appliedCoupons } = order;

  return (
    <div className="bg-gray-50 min-h-screen py-10">
      <div className="max-w-3xl mx-auto px-4">

        {/* ── Header ─────────────────────────────────────────────────────── */}
        <div className="bg-white rounded-2xl shadow-sm p-8 text-center mb-6">
          <FontAwesomeIcon icon={faCheckCircle} className="text-green-500 text-6xl mb-4" />
          <h1 className="text-3xl font-black text-gray-900 mb-2">Đặt hàng thành công!</h1>
          <p className="text-gray-500 mb-1">
            Mã đơn hàng: <span className="font-semibold text-gray-800 font-mono">#{order.id.slice(0, 8).toUpperCase()}</span>
          </p>
          <p className="text-gray-500 text-sm">
            Đặt lúc: {new Date(order.createdAt).toLocaleString('vi-VN')}
          </p>
        </div>

        {/* ── Order Items ─────────────────────────────────────────────────── */}
        <div className="bg-white rounded-2xl shadow-sm p-6 mb-6">
          <h2 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
            <FontAwesomeIcon icon={faBox} className="text-primary" /> Sản phẩm đã đặt
          </h2>
          <div className="divide-y divide-gray-100">
            {items.map((item) => (
              <div key={item.productId} className="flex justify-between items-center py-3">
                <div>
                  <p className="font-medium text-gray-800">{item.productName}</p>
                  <p className="text-sm text-gray-500">x{item.quantity} × {item.price.toLocaleString('vi-VN')}đ</p>
                </div>
                <span className="font-semibold text-gray-900">{item.totalPrice.toLocaleString('vi-VN')}đ</span>
              </div>
            ))}
          </div>
        </div>

        {/* ── Shipping & Payment Info ─────────────────────────────────────── */}
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6 mb-6">
          {/* Shipping */}
          <div className="bg-white rounded-2xl shadow-sm p-6">
            <h2 className="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
              <FontAwesomeIcon icon={faTruck} className="text-primary" /> Thông tin giao hàng
            </h2>
            <p className="font-semibold text-gray-800">{shippingInfo.fullName}</p>
            <p className="text-gray-600 text-sm mt-1">{shippingInfo.phone}</p>
            <p className="text-gray-600 text-sm mt-1">
              {shippingInfo.addressLine}, {shippingInfo.ward}, {shippingInfo.city}, {shippingInfo.district}
            </p>
            <div className="mt-3 pt-3 border-t border-gray-100">
              <p className="text-sm text-gray-700">
                <span className="font-medium">{shippingInfo.methodName}</span>
              </p>
              <p className="text-sm text-gray-500">
                Dự kiến {shippingInfo.estimatedDeliveryMin}–{shippingInfo.estimatedDeliveryMax} ngày làm việc
              </p>
            </div>
          </div>

          {/* Payment */}
          <div className="bg-white rounded-2xl shadow-sm p-6">
            <h2 className="text-lg font-bold text-gray-800 mb-3 flex items-center gap-2">
              <FontAwesomeIcon icon={faCreditCard} className="text-primary" /> Thanh toán
            </h2>
            <p className="font-semibold text-gray-800">{paymentInfo.methodName}</p>
            <span className={`mt-2 inline-block px-3 py-1 rounded-full text-xs font-semibold ${paymentInfo.status === 'PENDING' ? 'bg-yellow-100 text-yellow-700' : 'bg-green-100 text-green-700'}`}>
              {paymentInfo.status === 'PENDING' ? 'Chờ thanh toán' : 'Đã thanh toán'}
            </span>
          </div>
        </div>

        {/* ── Pricing Summary ─────────────────────────────────────────────── */}
        <div className="bg-white rounded-2xl shadow-sm p-6 mb-6">
          <h2 className="text-lg font-bold text-gray-800 mb-4">Tổng kết đơn hàng</h2>

          {appliedCoupons.length > 0 && (
            <div className="mb-3 flex flex-wrap gap-2">
              {appliedCoupons.map((c) => (
                <span key={c.couponId} className="flex items-center gap-1 bg-green-50 text-green-700 text-xs font-semibold px-3 py-1 rounded-full border border-green-200">
                  <FontAwesomeIcon icon={faTag} /> {c.code} (−{c.discountAmount.toLocaleString('vi-VN')}đ)
                </span>
              ))}
            </div>
          )}

          <div className="space-y-2 text-sm text-gray-600">
            <div className="flex justify-between">
              <span>Tiền hàng</span>
              <span>{pricingInfo.subtotal.toLocaleString('vi-VN')}đ</span>
            </div>
            <div className="flex justify-between">
              <span>Phí vận chuyển</span>
              <span>{pricingInfo.shippingFee.toLocaleString('vi-VN')}đ</span>
            </div>
            {pricingInfo.discount > 0 && (
              <div className="flex justify-between text-green-600">
                <span>Giảm giá (coupon)</span>
                <span>−{pricingInfo.discount.toLocaleString('vi-VN')}đ</span>
              </div>
            )}
            <div className="flex justify-between font-black text-lg text-gray-900 border-t border-gray-100 pt-3 mt-2">
              <span>Tổng thanh toán</span>
              <span className="text-primary">{pricingInfo.finalPrice.toLocaleString('vi-VN')}đ</span>
            </div>
          </div>
        </div>

        {/* ── Actions ─────────────────────────────────────────────────────── */}
        <div className="flex gap-4">
          <Link
            to="/"
            className="flex-1 text-center bg-white border border-primary text-primary font-semibold py-3 rounded-xl hover:bg-primary hover:text-white transition"
          >
            Tiếp tục mua sắm
          </Link>
        </div>
      </div>
    </div>
  );
};

export default OrderSuccessPage;
