export interface Category {
  id: string;
  name: string;
  description?: string;
  createdAt?: string;
}

export interface Product {
  id: string;
  name: string;
  price: number;
  description?: string;
  status: string;
  slug: string;
  createdAt: string;
  category: Category;
  stockQuantity: number;
  thumbnailImage: string;
  images?: string[];
}

export interface User {
  email: string;
  userId: string;
}

export interface CartItem {
  productId: string;
  productName: string;
  productPrice: number;
  thumbnailImage: string;
  quantity: number;
  subtotal: number;
}

export interface Address {
  id: string;
  addressLine: string;
  city: string;
  district: string;
  ward: string;
  isDefault: boolean;
  userId: string;
}

export interface ShippingMethod {
  id: string;
  code: string;
  name: string;
  description: string;
  baseFee: number;
  estimatedDaysMin: number;
  estimatedDaysMax: number;
  isActive: boolean;
  createdAt: string;
}

export interface PaymentMethod {
  id: string;
  code: string;
  name: string;
  isActive: boolean;
  createdAt: string;
}

export interface Coupon {
  id: string;                 // UUID returned by GET /api/coupons/valid
  code: string;
  discountValue: number;
  discountType: 'FIXED' | 'PERCENT';
  minOrderValue: number;
  maxDiscount: number | null;
  scope: 'ORDER' | 'SHIPPING';
  startDate: string;
  expiryDate: string;
  remainingUsage: number;
  applicableToCurrentCart: boolean;
}

export interface OrderRequest {
  addressId: string;
  shippingMethodId: string;
  paymentMethodId: string;
  shippingFullName: string;
  shippingPhone: string;
  couponIds: string[];
}

export interface OrderItem {
  productId: string;
  productName: string;
  quantity: number;
  price: number;
  totalPrice: number;
}

export interface OrderResponse {
  id: string;
  status: string;
  shippingInfo: {
    fullName: string;
    phone: string;
    addressLine: string;
    city: string;
    district: string;
    ward: string;
    methodName: string;
    shippingFee: number;
    estimatedDeliveryMin: number;
    estimatedDeliveryMax: number;
  };
  paymentInfo: {
    methodName: string;
    status: string;
  };
  items: OrderItem[];
  pricingInfo: {
    subtotal: number;
    shippingFee: number;
    discount: number;
    couponDiscount: number;
    finalPrice: number;
  };
  createdAt: string;
  updatedAt: string;
  appliedCoupons: {
    couponId: string;
    code: string;
    discountAmount: number;
  }[];
}