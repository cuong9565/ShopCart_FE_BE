import { useState, useEffect } from 'react';
import { addressService } from '../services/addressService';
import { shippingService } from '../services/shippingService';
import { paymentService } from '../services/paymentService';
import { couponService } from '../services/couponService';
import { orderService } from '../services/orderService';
import { showToast } from '../utils/toast';
import { useAuth } from '../context/AuthContext';
import type {
  Address,
  ShippingMethod,
  PaymentMethod,
  Coupon,
  OrderRequest,
  OrderResponse,
} from '../types';

export interface CheckoutFormData {
  shippingFullName: string;
  shippingPhone: string;
  selectedAddressId: string;
  selectedShippingMethodId: string;
  selectedPaymentMethodId: string;
  selectedCouponCodes: string[];
}

export interface CheckoutFormErrors {
  shippingFullName?: string;
  shippingPhone?: string;
  selectedAddressId?: string;
  selectedShippingMethodId?: string;
  selectedPaymentMethodId?: string;
}

// ─── Vietnamese phone number regex ───────────────────────────────────────────
const PHONE_REGEX = /^(0|\+84)(3[2-9]|5[6-9]|7[06-9]|8[0-9]|9[0-9])[0-9]{7}$/;

export const useCheckout = () => {
  const { user } = useAuth();
  const [addresses, setAddresses] = useState<Address[]>([]);
  const [shippingMethods, setShippingMethods] = useState<ShippingMethod[]>([]);
  const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
  const [coupons, setCoupons] = useState<Coupon[]>([]);

  const [dataLoading, setDataLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);

  const [formData, setFormData] = useState<CheckoutFormData>({
    shippingFullName: user?.fullName || '',
    shippingPhone: user?.phone || '',
    selectedAddressId: '',
    selectedShippingMethodId: '',
    selectedPaymentMethodId: '',
    selectedCouponCodes: [],
  });

  // Pre-populate fields with logged-in user details once loaded
  useEffect(() => {
    if (user) {
      setFormData((prev) => ({
        ...prev,
        shippingFullName: prev.shippingFullName || user.fullName || '',
        shippingPhone: prev.shippingPhone || user.phone || '',
      }));
    }
  }, [user]);

  const [errors, setErrors] = useState<CheckoutFormErrors>({});

  // ─── Fetch all initial data in parallel ────────────────────────────────────
  useEffect(() => {
    const fetchAll = async () => {
      setDataLoading(true);
      try {
        const [addrData, shipData, payData, couponData] = await Promise.all([
          addressService.getAddresses().catch(() => []),
          shippingService.getShippingMethods().catch(() => []),
          paymentService.getPaymentMethods().catch(() => []),
          couponService.getValidCoupons().catch(() => []),
        ]);

        setAddresses(addrData);
        setShippingMethods(shipData);
        setPaymentMethods(payData);
        setCoupons(couponData);

        // Pre-select defaults if available
        setFormData((prev) => ({
          ...prev,
          selectedAddressId: addrData.find((a: Address) => a.isDefault)?.id ?? addrData[0]?.id ?? '',
          selectedShippingMethodId: shipData[0]?.id ?? '',
          selectedPaymentMethodId: payData[0]?.id ?? '',
        }));
      } catch {
        showToast('Không thể tải dữ liệu thanh toán', 'error');
      } finally {
        setDataLoading(false);
      }
    };

    fetchAll();
  }, []);

  // ─── Derived pricing ───────────────────────────────────────────────────────
  const selectedShipping = shippingMethods.find(
    (s) => s.id === formData.selectedShippingMethodId
  );

  const selectedCoupons = coupons.filter((c) =>
    formData.selectedCouponCodes.includes(c.code)
  );

  // ─── Validation ───────────────────────────────────────────────────────────
  const validate = (): boolean => {
    const errs: CheckoutFormErrors = {};

    if (!formData.shippingFullName.trim()) {
      errs.shippingFullName = 'Vui lòng nhập họ tên người nhận';
    } else if (formData.shippingFullName.trim().length < 2) {
      errs.shippingFullName = 'Họ tên phải có ít nhất 2 ký tự';
    }

    if (!formData.shippingPhone.trim()) {
      errs.shippingPhone = 'Vui lòng nhập số điện thoại';
    } else if (!PHONE_REGEX.test(formData.shippingPhone.trim())) {
      errs.shippingPhone = 'Số điện thoại không hợp lệ (VD: 0912345678)';
    }

    if (!formData.selectedAddressId) {
      errs.selectedAddressId = 'Vui lòng chọn địa chỉ giao hàng';
    }

    if (!formData.selectedShippingMethodId) {
      errs.selectedShippingMethodId = 'Vui lòng chọn phương thức vận chuyển';
    }

    if (!formData.selectedPaymentMethodId) {
      errs.selectedPaymentMethodId = 'Vui lòng chọn phương thức thanh toán';
    }

    setErrors(errs);
    return Object.keys(errs).length === 0;
  };

  // ─── Field updater ────────────────────────────────────────────────────────
  const updateField = <K extends keyof CheckoutFormData>(
    key: K,
    value: CheckoutFormData[K]
  ) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
    // Clear the error for the field being changed
    setErrors((prev) => ({ ...prev, [key]: undefined }));
  };

  // ─── Toggle coupon selection ──────────────────────────────────────────────
  const toggleCoupon = (code: string) => {
    setFormData((prev) => {
      const already = prev.selectedCouponCodes.includes(code);
      return {
        ...prev,
        selectedCouponCodes: already
          ? prev.selectedCouponCodes.filter((c) => c !== code)
          : [...prev.selectedCouponCodes, code],
      };
    });
  };

  // ─── Add new address then re-fetch list ───────────────────────────────────
  const addAddress = async (
    data: Omit<Address, 'id' | 'isDefault' | 'userId'>
  ): Promise<Address | null> => {
    try {
      const newAddr = await addressService.addAddress(data);
      setAddresses((prev) => [...prev, newAddr]);
      updateField('selectedAddressId', newAddr.id);
      showToast('Đã thêm địa chỉ mới', 'success');
      return newAddr;
    } catch {
      showToast('Không thể thêm địa chỉ', 'error');
      return null;
    }
  };

  // ─── Submit order ─────────────────────────────────────────────────────────
  const submitOrder = async (): Promise<OrderResponse | null> => {
    if (!validate()) return null;

    // GET /api/coupons/valid returns 'id' (UUID) for each coupon.
    // We send those UUIDs to POST /api/orders as couponIds.
    const couponIds = coupons
      .filter((c) => formData.selectedCouponCodes.includes(c.code))
      .map((c) => c.id)           // id is the UUID returned by the backend
      .filter((id): id is string => !!id);  // safety: skip any without id

    const payload: OrderRequest = {
      addressId: formData.selectedAddressId,
      shippingMethodId: formData.selectedShippingMethodId,
      paymentMethodId: formData.selectedPaymentMethodId,
      shippingFullName: formData.shippingFullName.trim(),
      shippingPhone: formData.shippingPhone.trim(),
      couponIds,
    };


    try {
      setSubmitting(true);
      const order = await orderService.placeOrder(payload);
      showToast('Đặt hàng thành công!', 'success');
      return order;
    } catch (err: any) {
      const msg: string =
        err?.response?.data?.error ||
        err?.response?.data?.message ||
        'Đặt hàng thất bại, vui lòng thử lại';
      showToast(msg, 'error');
      return null;
    } finally {
      setSubmitting(false);
    }
  };

  return {
    // Data
    addresses,
    shippingMethods,
    paymentMethods,
    coupons,
    selectedShipping,
    selectedCoupons,
    // State
    dataLoading,
    submitting,
    formData,
    errors,
    // Actions
    updateField,
    toggleCoupon,
    addAddress,
    submitOrder,
    validate,
  };
};
