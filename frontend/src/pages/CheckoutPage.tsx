import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import {
  faMapMarkerAlt,
  faTruck,
  faCreditCard,
  faTag,
  faPlus,
  faChevronDown,
  faChevronUp,
  faCheck,
  faSpinner,
  faShoppingBag,
} from '@fortawesome/free-solid-svg-icons';
import { useCart } from '../hooks/useCart';
import { useCheckout } from '../hooks/useCheckout';
import type { Address } from '../types';

// ─── Add-address modal ───────────────────────────────────────────────────────
interface AddressFormState {
  addressLine: string;
  city: string;
  district: string;
  ward: string;
}
const EMPTY_ADDR: AddressFormState = { addressLine: '', city: '', district: '', ward: '' };

const AddAddressModal = ({
  onClose,
  onSave,
}: {
  onClose: () => void;
  onSave: (data: AddressFormState) => Promise<void>;
}) => {
  const [form, setForm] = useState<AddressFormState>(EMPTY_ADDR);
  const [saving, setSaving] = useState(false);
  const [errs, setErrs] = useState<Partial<AddressFormState>>({});

  const validate = () => {
    const e: Partial<AddressFormState> = {};
    if (!form.addressLine.trim()) e.addressLine = 'Vui lòng nhập địa chỉ cụ thể';
    if (!form.ward.trim()) e.ward = 'Vui lòng nhập phường/xã';
    if (!form.city.trim()) e.city = 'Vui lòng nhập quận/huyện';
    if (!form.district.trim()) e.district = 'Vui lòng nhập tỉnh/thành phố';
    setErrs(e);
    return Object.keys(e).length === 0;
  };

  const handleSave = async () => {
    if (!validate()) return;
    setSaving(true);
    await onSave(form);
    setSaving(false);
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm px-4">
      <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md p-6">
        <h3 className="text-xl font-bold text-gray-900 mb-5">Thêm địa chỉ mới</h3>

        {(
          [
            { key: 'addressLine', label: 'Địa chỉ cụ thể (số nhà, tên đường)', placeholder: 'VD: 123 Nguyễn Huệ, Phường Bến Thành' },
            { key: 'ward', label: 'Phường / Xã', placeholder: 'VD: Bến Thành' },
            { key: 'city', label: 'Quận / Huyện', placeholder: 'VD: Quận 1' },
            { key: 'district', label: 'Tỉnh / Thành phố', placeholder: 'VD: TP. Hồ Chí Minh' },
          ] as { key: keyof AddressFormState; label: string; placeholder: string }[]
        ).map(({ key, label, placeholder }) => (
          <div key={key} className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">{label}</label>
            <input
              type="text"
              value={form[key]}
              onChange={(e) => {
                setForm((prev) => ({ ...prev, [key]: e.target.value }));
                setErrs((prev) => ({ ...prev, [key]: undefined }));
              }}
              placeholder={placeholder}
              className={`w-full border rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/40 transition ${errs[key] ? 'border-red-400' : 'border-gray-200'}`}
            />
            {errs[key] && <p className="text-xs text-red-500 mt-1">{errs[key]}</p>}
          </div>
        ))}

        <div className="flex gap-3 mt-6">
          <button
            onClick={onClose}
            className="flex-1 py-2.5 rounded-xl border border-gray-300 text-gray-700 font-semibold hover:bg-gray-50 transition"
          >
            Hủy
          </button>
          <button
            onClick={handleSave}
            disabled={saving}
            className="flex-1 py-2.5 rounded-xl bg-primary text-white font-semibold hover:bg-primary-dark transition disabled:opacity-50"
          >
            {saving ? <FontAwesomeIcon icon={faSpinner} spin /> : 'Lưu địa chỉ'}
          </button>
        </div>
      </div>
    </div>
  );
};

// ─── Section wrapper ──────────────────────────────────────────────────────────
const Section = ({
  icon,
  title,
  children,
}: {
  icon: any;
  title: string;
  children: React.ReactNode;
}) => (
  <div className="bg-white rounded-2xl shadow-sm p-6 mb-5">
    <h2 className="text-base font-bold text-gray-800 mb-4 flex items-center gap-2">
      <FontAwesomeIcon icon={icon} className="text-primary" />
      {title}
    </h2>
    {children}
  </div>
);

// ─── Error text ───────────────────────────────────────────────────────────────
const FieldError = ({ msg }: { msg?: string }) =>
  msg ? <p className="text-xs text-red-500 mt-1">{msg}</p> : null;

// ─── Main CheckoutPage ────────────────────────────────────────────────────────
const CheckoutPage = () => {
  const navigate = useNavigate();
  const { cart, total, loading: cartLoading } = useCart(true);
  const [showAddModal, setShowAddModal] = useState(false);
  const [couponOpen, setCouponOpen] = useState(false);

  const {
    addresses,
    shippingMethods,
    paymentMethods,
    coupons,
    selectedShipping,
    dataLoading,
    submitting,
    formData,
    errors,
    updateField,
    toggleCoupon,
    addAddress,
    submitOrder,
  } = useCheckout();

  // ─── Derived ──────────────────────────────────────────────────────────────
  const shippingFee = selectedShipping?.baseFee ?? 0;

  const orderDiscount = coupons
    .filter((c) => formData.selectedCouponCodes.includes(c.code) && c.scope === 'ORDER')
    .reduce((sum, c) => {
      if (c.discountType === 'PERCENT') {
        const raw = (total * c.discountValue) / 100;
        return sum + (c.maxDiscount ? Math.min(raw, c.maxDiscount) : raw);
      }
      return sum + c.discountValue;
    }, 0);

  const shippingDiscount = coupons
    .filter((c) => formData.selectedCouponCodes.includes(c.code) && c.scope === 'SHIPPING')
    .reduce((sum, c) => sum + (c.discountType === 'FIXED' ? c.discountValue : (shippingFee * c.discountValue) / 100), 0);

  const finalShippingFee = Math.max(0, shippingFee - shippingDiscount);
  const finalPrice = Math.max(0, total - orderDiscount + finalShippingFee);

  // ─── Handlers ─────────────────────────────────────────────────────────────
  const handleAddAddress = async (data: AddressFormState) => {
    const result = await addAddress(data);
    if (result) setShowAddModal(false);
  };

  const handlePlaceOrder = async () => {
    const order = await submitOrder();
    if (order) {
      navigate('/order/success', { state: { order } });
    }
  };

  // ─── Loading ──────────────────────────────────────────────────────────────
  if (cartLoading || dataLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <FontAwesomeIcon icon={faSpinner} spin size="3x" className="text-primary" />
      </div>
    );
  }

  if (cart.length === 0) {
    return (
      <div className="max-w-xl mx-auto px-4 py-20 text-center">
        <FontAwesomeIcon icon={faShoppingBag} className="text-gray-300 text-6xl mb-6" />
        <h2 className="text-2xl font-bold text-gray-700 mb-4">Giỏ hàng của bạn đang trống</h2>
        <Link to="/" className="bg-primary text-white px-6 py-3 rounded-xl font-semibold hover:bg-primary-dark transition">
          Tiếp tục mua sắm
        </Link>
      </div>
    );
  }

  return (
    <div className="bg-gray-50 min-h-screen py-8">
      {showAddModal && (
        <AddAddressModal
          onClose={() => setShowAddModal(false)}
          onSave={handleAddAddress}
        />
      )}

      <div className="max-w-6xl mx-auto px-4">
        {/* Breadcrumb */}
        <nav className="text-sm text-gray-500 mb-6">
          <Link to="/" className="hover:text-primary transition">Trang chủ</Link>
          <span className="mx-2">/</span>
          <Link to="/cart" className="hover:text-primary transition">Giỏ hàng</Link>
          <span className="mx-2">/</span>
          <span className="text-gray-800 font-medium">Thanh toán</span>
        </nav>

        <h1 className="text-3xl font-black text-gray-900 mb-6">Thanh toán</h1>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

          {/* ═══════════════ LEFT COLUMN ════════════════════════════════════ */}
          <div className="lg:col-span-2">

            {/* ── 1. RECIPIENT INFO ─────────────────────────────────────── */}
            <Section icon={faMapMarkerAlt} title="Thông tin người nhận">
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-4 mb-5">
                {/* Full Name */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Họ và tên người nhận <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="text"
                    id="shippingFullName"
                    value={formData.shippingFullName}
                    onChange={(e) => updateField('shippingFullName', e.target.value)}
                    placeholder="Nguyễn Văn A"
                    className={`w-full border rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/40 transition ${errors.shippingFullName ? 'border-red-400' : 'border-gray-200'}`}
                  />
                  <FieldError msg={errors.shippingFullName} />
                </div>

                {/* Phone */}
                <div>
                  <label className="block text-sm font-medium text-gray-700 mb-1">
                    Số điện thoại <span className="text-red-500">*</span>
                  </label>
                  <input
                    type="tel"
                    id="shippingPhone"
                    value={formData.shippingPhone}
                    onChange={(e) => updateField('shippingPhone', e.target.value)}
                    placeholder="0912 345 678"
                    className={`w-full border rounded-xl px-4 py-2.5 text-sm focus:outline-none focus:ring-2 focus:ring-primary/40 transition ${errors.shippingPhone ? 'border-red-400' : 'border-gray-200'}`}
                  />
                  <FieldError msg={errors.shippingPhone} />
                </div>
              </div>

              {/* Address selection */}
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Địa chỉ giao hàng <span className="text-red-500">*</span>
              </label>

              {addresses.length === 0 ? (
                <p className="text-sm text-gray-500 mb-3">Bạn chưa có địa chỉ nào. Hãy thêm địa chỉ mới.</p>
              ) : (
                <div className="space-y-3 mb-3">
                  {addresses.map((addr: Address) => (
                    <label
                      key={addr.id}
                      className={`flex items-start gap-3 p-4 rounded-xl border-2 cursor-pointer transition ${formData.selectedAddressId === addr.id ? 'border-primary bg-blue-50' : 'border-gray-200 hover:border-primary/40'}`}
                    >
                      <input
                        type="radio"
                        name="address"
                        value={addr.id}
                        checked={formData.selectedAddressId === addr.id}
                        onChange={() => updateField('selectedAddressId', addr.id)}
                        className="mt-0.5 accent-primary"
                      />
                      <div className="flex-1 min-w-0">
                        <p className="text-sm font-medium text-gray-800 truncate">
                          {addr.addressLine}
                        </p>
                        <p className="text-xs text-gray-500">{addr.ward}, {addr.city}, {addr.district}</p>
                        {addr.isDefault && (
                          <span className="mt-1 inline-block bg-primary/10 text-primary text-xs font-semibold px-2 py-0.5 rounded-full">
                            Mặc định
                          </span>
                        )}
                      </div>
                    </label>
                  ))}
                </div>
              )}

              <FieldError msg={errors.selectedAddressId} />

              <button
                onClick={() => setShowAddModal(true)}
                className="mt-2 flex items-center gap-2 text-primary text-sm font-semibold hover:underline"
              >
                <FontAwesomeIcon icon={faPlus} /> Thêm địa chỉ mới
              </button>
            </Section>

            {/* ── 2. SHIPPING METHOD ────────────────────────────────────── */}
            <Section icon={faTruck} title="Phương thức vận chuyển">
              {shippingMethods.length === 0 ? (
                <p className="text-sm text-gray-500">Không có phương thức vận chuyển nào</p>
              ) : (
                <div className="space-y-3">
                  {shippingMethods.map((method) => (
                    <label
                      key={method.id}
                      className={`flex items-center gap-3 p-4 rounded-xl border-2 cursor-pointer transition ${formData.selectedShippingMethodId === method.id ? 'border-primary bg-blue-50' : 'border-gray-200 hover:border-primary/40'}`}
                    >
                      <input
                        type="radio"
                        name="shipping"
                        value={method.id}
                        checked={formData.selectedShippingMethodId === method.id}
                        onChange={() => updateField('selectedShippingMethodId', method.id)}
                        className="accent-primary"
                      />
                      <div className="flex-1">
                        <p className="text-sm font-semibold text-gray-800">{method.name}</p>
                        <p className="text-xs text-gray-500">
                          {method.description} • {method.estimatedDaysMin}–{method.estimatedDaysMax} ngày làm việc
                        </p>
                      </div>
                      <span className="text-sm font-bold text-gray-800 whitespace-nowrap">
                        {method.baseFee.toLocaleString('vi-VN')}đ
                      </span>
                    </label>
                  ))}
                </div>
              )}
              <FieldError msg={errors.selectedShippingMethodId} />
            </Section>

            {/* ── 3. PAYMENT METHOD ─────────────────────────────────────── */}
            <Section icon={faCreditCard} title="Phương thức thanh toán">
              {paymentMethods.length === 0 ? (
                <p className="text-sm text-gray-500">Không có phương thức thanh toán nào</p>
              ) : (
                <div className="space-y-3">
                  {paymentMethods.map((method) => (
                    <label
                      key={method.id}
                      className={`flex items-center gap-3 p-4 rounded-xl border-2 cursor-pointer transition ${formData.selectedPaymentMethodId === method.id ? 'border-primary bg-blue-50' : 'border-gray-200 hover:border-primary/40'}`}
                    >
                      <input
                        type="radio"
                        name="payment"
                        value={method.id}
                        checked={formData.selectedPaymentMethodId === method.id}
                        onChange={() => updateField('selectedPaymentMethodId', method.id)}
                        className="accent-primary"
                      />
                      <span className="text-sm font-medium text-gray-800">{method.name}</span>
                    </label>
                  ))}
                </div>
              )}
              <FieldError msg={errors.selectedPaymentMethodId} />
            </Section>

            {/* ── 4. COUPON ─────────────────────────────────────────────── */}
            <div className="bg-white rounded-2xl shadow-sm mb-5">
              <button
                data-testid="coupon-accordion-btn"
                onClick={() => setCouponOpen((v) => !v)}
                className="w-full flex items-center justify-between p-6 text-left"
              >
                <span className="text-base font-bold text-gray-800 flex items-center gap-2">
                  <FontAwesomeIcon icon={faTag} className="text-primary" />
                  Mã giảm giá
                  {formData.selectedCouponCodes.length > 0 && (
                    <span className="ml-1 bg-green-100 text-green-700 text-xs font-bold px-2 py-0.5 rounded-full">
                      {formData.selectedCouponCodes.length} đã chọn
                    </span>
                  )}
                </span>
                <FontAwesomeIcon icon={couponOpen ? faChevronUp : faChevronDown} className="text-gray-400" />
              </button>

              {couponOpen && (
                <div className="px-6 pb-6">
                  {coupons.length === 0 ? (
                    <p className="text-sm text-gray-500">Không có mã giảm giá nào khả dụng</p>
                  ) : (
                    <div className="space-y-3">
                      {coupons.map((coupon) => {
                        const selected = formData.selectedCouponCodes.includes(coupon.code);
                        const applicable = coupon.applicableToCurrentCart;
                        return (
                          <div
                            key={coupon.code}
                            data-testid={`coupon-item-${coupon.code}`}
                            onClick={() => applicable && toggleCoupon(coupon.code)}
                            className={`relative p-4 rounded-xl border-2 transition ${selected ? 'border-green-400 bg-green-50' : applicable ? 'border-gray-200 hover:border-green-300 cursor-pointer' : 'border-gray-100 opacity-50 cursor-not-allowed bg-gray-50'}`}
                          >
                            {selected && (
                              <FontAwesomeIcon
                                icon={faCheck}
                                className="absolute top-3 right-3 text-green-500"
                              />
                            )}
                            <div className="flex items-start justify-between pr-6">
                              <div>
                                <p className="font-bold text-gray-800 font-mono text-sm">{coupon.code}</p>
                                <p className="text-xs text-gray-600 mt-0.5">
                                  {coupon.discountType === 'PERCENT'
                                    ? `Giảm ${coupon.discountValue}%${coupon.maxDiscount ? ` (tối đa ${coupon.maxDiscount.toLocaleString('vi-VN')}đ)` : ''}`
                                    : `Giảm ${coupon.discountValue.toLocaleString('vi-VN')}đ`}
                                  {coupon.scope === 'SHIPPING' ? ' phí vận chuyển' : ' đơn hàng'}
                                </p>
                                {coupon.minOrderValue > 0 && (
                                  <p className="text-xs text-gray-400 mt-0.5">
                                    Đơn tối thiểu {coupon.minOrderValue.toLocaleString('vi-VN')}đ
                                  </p>
                                )}
                              </div>
                              <div className="text-right">
                                <p className="text-xs text-gray-400">
                                  Còn {coupon.remainingUsage} lần
                                </p>
                                <p className="text-xs text-gray-400">
                                  HSD: {new Date(coupon.expiryDate).toLocaleDateString('vi-VN')}
                                </p>
                              </div>
                            </div>
                            {!applicable && (
                              <p className="text-xs text-red-400 mt-1">
                                Đơn hàng chưa đạt giá trị tối thiểu
                              </p>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              )}
            </div>
          </div>

          {/* ═══════════════ RIGHT COLUMN (Order Summary) ═══════════════════ */}
          <div className="lg:col-span-1">
            <div className="bg-white rounded-2xl shadow-sm p-6 sticky top-6">
              <h2 className="text-lg font-bold text-gray-900 mb-5">Tóm tắt đơn hàng</h2>

              {/* Cart items */}
              <div className="divide-y divide-gray-100 mb-5 max-h-56 overflow-y-auto">
                {cart.map((item) => (
                  <div key={item.productId} className="flex gap-3 py-3">
                    <img
                      src={item.thumbnailImage}
                      alt={item.productName}
                      className="w-14 h-14 object-cover rounded-lg flex-shrink-0"
                    />
                    <div className="flex-1 min-w-0">
                      <p className="text-sm font-medium text-gray-800 line-clamp-2">{item.productName}</p>
                      <p className="text-xs text-gray-500">x{item.quantity}</p>
                    </div>
                    <span className="text-sm font-semibold text-gray-800 whitespace-nowrap">
                      {item.subtotal.toLocaleString('vi-VN')}đ
                    </span>
                  </div>
                ))}
              </div>

              {/* Pricing breakdown */}
              <div className="space-y-2 text-sm border-t border-gray-100 pt-4">
                <div className="flex justify-between text-gray-600">
                  <span>Tiền hàng</span>
                  <span>{total.toLocaleString('vi-VN')}đ</span>
                </div>
                <div className="flex justify-between text-gray-600">
                  <span>Phí vận chuyển</span>
                  <span>
                    {shippingFee > 0
                      ? `${shippingFee.toLocaleString('vi-VN')}đ`
                      : <span className="text-gray-400">Chưa chọn</span>}
                  </span>
                </div>
                {orderDiscount > 0 && (
                  <div className="flex justify-between text-green-600">
                    <span>Giảm giá đơn hàng</span>
                    <span>−{orderDiscount.toLocaleString('vi-VN')}đ</span>
                  </div>
                )}
                {shippingDiscount > 0 && (
                  <div className="flex justify-between text-green-600">
                    <span>Giảm phí ship</span>
                    <span>−{shippingDiscount.toLocaleString('vi-VN')}đ</span>
                  </div>
                )}
                <div className="flex justify-between font-black text-base text-gray-900 border-t border-gray-100 pt-3 mt-1">
                  <span>Tổng thanh toán</span>
                  <span className="text-primary text-lg">{finalPrice.toLocaleString('vi-VN')}đ</span>
                </div>
              </div>

              {/* Place order button */}
              <button
                id="place-order-btn"
                onClick={handlePlaceOrder}
                disabled={submitting}
                className="w-full mt-6 bg-primary hover:bg-primary-dark text-white font-bold py-4 rounded-xl transition-all shadow-lg shadow-primary/20 flex items-center justify-center gap-2 disabled:opacity-60 disabled:cursor-not-allowed"
              >
                {submitting ? (
                  <><FontAwesomeIcon icon={faSpinner} spin /> Đang xử lý...</>
                ) : (
                  'Đặt hàng'
                )}
              </button>

              <p className="text-center text-xs text-gray-400 mt-3">
                Bằng cách đặt hàng, bạn đồng ý với điều khoản dịch vụ của chúng tôi.
              </p>
            </div>
          </div>

        </div>
      </div>
    </div>
  );
};

export default CheckoutPage;
